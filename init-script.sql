-- =====================================================
-- Humanis / miCarrera - Migration Script (legacydb -> humanis)
-- =====================================================
-- Purpose:
--   Migrate an existing legacydb schema in-place to the Humanis target schema.
--   Preserves existing data from the MVP core tables.
--
-- Notes:
--   - Target: Supabase PostgreSQL (auth.users + auth.uid()).
--   - This script does NOT drop core data tables.
--   - If careers.start_date has invalid text values, they are converted to NULL.

BEGIN;

-- =====================================================
-- EXTENSIONS
-- =====================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- ENSURE CORE TABLES EXIST (safe on legacy and empty envs)
-- =====================================================
CREATE TABLE IF NOT EXISTS public.careers (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
	name TEXT NOT NULL,
	institution TEXT NOT NULL,
	status TEXT NOT NULL DEFAULT 'NOT_STARTED'
		CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'PAUSED', 'COMPLETED')),
	start_date DATE,
	has_hours BOOLEAN NOT NULL DEFAULT FALSE,
	has_credits BOOLEAN NOT NULL DEFAULT FALSE,
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.subjects (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	career_id UUID NOT NULL REFERENCES public.careers(id) ON DELETE CASCADE,
	name TEXT NOT NULL,
	code TEXT,
	status TEXT NOT NULL
		CHECK (status IN ('pendiente', 'cursando', 'regular', 'aprobada', 'libre')),
	grade INTEGER CHECK (grade >= 0 AND grade <= 10),
	approval_method TEXT
		CHECK (approval_method IN ('promocion', 'examen_final', 'examen_libre')),
	correlatives TEXT[] DEFAULT '{}',
	year INTEGER CHECK (year >= 0),
	semester INTEGER CHECK (semester >= 0),
	is_entrance_course BOOLEAN NOT NULL DEFAULT FALSE,
	hours INTEGER CHECK (hours >= 0),
	credits INTEGER CHECK (credits >= 0),
	color TEXT NOT NULL DEFAULT '#3B82F6',
	grade_required_for_promotion NUMERIC(4,2)
		CHECK (grade_required_for_promotion >= 0 AND grade_required_for_promotion <= 10),
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.subject_modules (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	subject_id UUID NOT NULL REFERENCES public.subjects(id) ON DELETE CASCADE,
	name TEXT NOT NULL,
	grade NUMERIC(4,2) CHECK (grade >= 0 AND grade <= 10),
	module_order INTEGER NOT NULL DEFAULT 0,
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.equivalences (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
	type TEXT NOT NULL CHECK (type IN ('total', 'parcial')),
	subject_a_id UUID NOT NULL REFERENCES public.subjects(id) ON DELETE CASCADE,
	subject_b_id UUID NOT NULL REFERENCES public.subjects(id) ON DELETE CASCADE,
	notes TEXT,
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	CONSTRAINT different_subjects CHECK (subject_a_id <> subject_b_id)
);

-- =====================================================
-- CAREERS NORMALIZATION (legacy -> humanis)
-- =====================================================

-- Drop any legacy/new status checks first so status values can be transformed.
DO $$
DECLARE
	c RECORD;
BEGIN
	IF to_regclass('public.careers') IS NULL THEN
		RETURN;
	END IF;

	FOR c IN
		SELECT conname
		FROM pg_constraint
		WHERE conrelid = 'public.careers'::regclass
			AND contype = 'c'
			AND pg_get_constraintdef(oid) ILIKE '%status%'
	LOOP
		EXECUTE format('ALTER TABLE public.careers DROP CONSTRAINT %I', c.conname);
	END LOOP;
END $$;

-- Convert legacy TEXT start_date into DATE when needed.
DO $$
DECLARE
	v_data_type TEXT;
BEGIN
	SELECT c.data_type
	INTO v_data_type
	FROM information_schema.columns c
	WHERE c.table_schema = 'public'
		AND c.table_name = 'careers'
		AND c.column_name = 'start_date';

	IF v_data_type = 'text' THEN
		ALTER TABLE public.careers ADD COLUMN IF NOT EXISTS start_date_tmp DATE;

		UPDATE public.careers
		SET start_date_tmp = CASE
			WHEN start_date IS NULL OR btrim(start_date) = '' THEN NULL
			WHEN start_date ~ '^\\d{4}-\\d{2}-\\d{2}$' THEN to_date(start_date, 'YYYY-MM-DD')
			WHEN start_date ~ '^\\d{4}/\\d{2}/\\d{2}$' THEN to_date(start_date, 'YYYY/MM/DD')
			WHEN start_date ~ '^\\d{2}/\\d{2}/\\d{4}$' THEN to_date(start_date, 'DD/MM/YYYY')
			WHEN start_date ~ '^\\d{2}-\\d{2}-\\d{4}$' THEN to_date(start_date, 'DD-MM-YYYY')
			WHEN start_date ~ '^\\d{4}-\\d{2}-\\d{2}T.*$' THEN to_date(substr(start_date, 1, 10), 'YYYY-MM-DD')
			ELSE NULL
		END;

		ALTER TABLE public.careers DROP COLUMN start_date;
		ALTER TABLE public.careers RENAME COLUMN start_date_tmp TO start_date;
	ELSIF v_data_type IS NULL THEN
		ALTER TABLE public.careers ADD COLUMN start_date DATE;
	END IF;
END $$;

UPDATE public.careers
SET status = CASE lower(status)
	WHEN 'no_iniciada' THEN 'NOT_STARTED'
	WHEN 'en_curso' THEN 'IN_PROGRESS'
	WHEN 'pausada' THEN 'PAUSED'
	WHEN 'finalizada' THEN 'COMPLETED'
	WHEN 'not_started' THEN 'NOT_STARTED'
	WHEN 'in_progress' THEN 'IN_PROGRESS'
	WHEN 'paused' THEN 'PAUSED'
	WHEN 'completed' THEN 'COMPLETED'
	ELSE status
END;

UPDATE public.careers
SET status = 'NOT_STARTED'
WHERE status IS NULL
	 OR status NOT IN ('NOT_STARTED', 'IN_PROGRESS', 'PAUSED', 'COMPLETED');

UPDATE public.careers
SET has_hours = COALESCE(has_hours, FALSE),
		has_credits = COALESCE(has_credits, FALSE),
		created_at = COALESCE(created_at, NOW()),
		updated_at = COALESCE(updated_at, NOW());

ALTER TABLE public.careers
	ALTER COLUMN status SET DEFAULT 'NOT_STARTED',
	ALTER COLUMN status SET NOT NULL,
	ALTER COLUMN start_date TYPE DATE USING start_date,
	ALTER COLUMN has_hours SET DEFAULT FALSE,
	ALTER COLUMN has_hours SET NOT NULL,
	ALTER COLUMN has_credits SET DEFAULT FALSE,
	ALTER COLUMN has_credits SET NOT NULL,
	ALTER COLUMN created_at SET DEFAULT NOW(),
	ALTER COLUMN created_at SET NOT NULL,
	ALTER COLUMN updated_at SET DEFAULT NOW(),
	ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE public.careers
	ADD CONSTRAINT careers_status_check
	CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'PAUSED', 'COMPLETED'));

-- =====================================================
-- SUBJECTS NORMALIZATION (legacy -> humanis)
-- =====================================================
ALTER TABLE public.subjects
	ADD COLUMN IF NOT EXISTS color TEXT,
	ADD COLUMN IF NOT EXISTS grade_required_for_promotion NUMERIC(4,2);

DO $$
DECLARE
	c RECORD;
BEGIN
	FOR c IN
		SELECT conname
		FROM pg_constraint
		WHERE conrelid = 'public.subjects'::regclass
			AND contype = 'c'
			AND (
				pg_get_constraintdef(oid) ILIKE '%status%'
				OR pg_get_constraintdef(oid) ILIKE '%approval_method%'
				OR pg_get_constraintdef(oid) ILIKE '%grade%'
				OR pg_get_constraintdef(oid) ILIKE '%year%'
				OR pg_get_constraintdef(oid) ILIKE '%semester%'
				OR pg_get_constraintdef(oid) ILIKE '%hours%'
				OR pg_get_constraintdef(oid) ILIKE '%credits%'
			)
	LOOP
		EXECUTE format('ALTER TABLE public.subjects DROP CONSTRAINT %I', c.conname);
	END LOOP;
END $$;

UPDATE public.subjects
SET status = 'pendiente'
WHERE status IS NULL
	 OR status NOT IN ('pendiente', 'cursando', 'regular', 'aprobada', 'libre');

UPDATE public.subjects
SET approval_method = NULL
WHERE approval_method IS NOT NULL
	AND approval_method NOT IN ('promocion', 'examen_final', 'examen_libre');

UPDATE public.subjects
SET grade = NULL
WHERE grade IS NOT NULL AND (grade < 0 OR grade > 10);

UPDATE public.subjects
SET year = NULL
WHERE year IS NOT NULL AND year < 0;

UPDATE public.subjects
SET semester = NULL
WHERE semester IS NOT NULL AND semester < 0;

UPDATE public.subjects
SET hours = NULL
WHERE hours IS NOT NULL AND hours < 0;

UPDATE public.subjects
SET credits = NULL
WHERE credits IS NOT NULL AND credits < 0;

UPDATE public.subjects
SET grade_required_for_promotion = NULL
WHERE grade_required_for_promotion IS NOT NULL
	AND (grade_required_for_promotion < 0 OR grade_required_for_promotion > 10);

UPDATE public.subjects
SET correlatives = COALESCE(correlatives, '{}'::text[]),
		is_entrance_course = COALESCE(is_entrance_course, FALSE),
		color = COALESCE(NULLIF(color, ''), '#3B82F6'),
		created_at = COALESCE(created_at, NOW()),
		updated_at = COALESCE(updated_at, NOW());

ALTER TABLE public.subjects
	ALTER COLUMN status SET NOT NULL,
	ALTER COLUMN correlatives SET DEFAULT '{}'::text[],
	ALTER COLUMN is_entrance_course SET DEFAULT FALSE,
	ALTER COLUMN is_entrance_course SET NOT NULL,
	ALTER COLUMN color SET DEFAULT '#3B82F6',
	ALTER COLUMN color SET NOT NULL,
	ALTER COLUMN created_at SET DEFAULT NOW(),
	ALTER COLUMN created_at SET NOT NULL,
	ALTER COLUMN updated_at SET DEFAULT NOW(),
	ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE public.subjects
	ADD CONSTRAINT subjects_status_check
	CHECK (status IN ('pendiente', 'cursando', 'regular', 'aprobada', 'libre')),
	ADD CONSTRAINT subjects_approval_method_check
	CHECK (approval_method IN ('promocion', 'examen_final', 'examen_libre')),
	ADD CONSTRAINT subjects_grade_check
	CHECK (grade >= 0 AND grade <= 10),
	ADD CONSTRAINT subjects_year_check
	CHECK (year >= 0),
	ADD CONSTRAINT subjects_semester_check
	CHECK (semester >= 0),
	ADD CONSTRAINT subjects_hours_check
	CHECK (hours >= 0),
	ADD CONSTRAINT subjects_credits_check
	CHECK (credits >= 0),
	ADD CONSTRAINT subjects_grade_required_for_promotion_check
	CHECK (grade_required_for_promotion >= 0 AND grade_required_for_promotion <= 10);

-- =====================================================
-- SUBJECT_MODULES NORMALIZATION
-- =====================================================
DO $$
DECLARE
	c RECORD;
BEGIN
	FOR c IN
		SELECT conname
		FROM pg_constraint
		WHERE conrelid = 'public.subject_modules'::regclass
			AND contype = 'c'
			AND pg_get_constraintdef(oid) ILIKE '%grade%'
	LOOP
		EXECUTE format('ALTER TABLE public.subject_modules DROP CONSTRAINT %I', c.conname);
	END LOOP;
END $$;

UPDATE public.subject_modules
SET module_order = COALESCE(module_order, 0),
		created_at = COALESCE(created_at, NOW()),
		updated_at = COALESCE(updated_at, NOW());

ALTER TABLE public.subject_modules
	ALTER COLUMN module_order SET DEFAULT 0,
	ALTER COLUMN module_order SET NOT NULL,
	ALTER COLUMN created_at SET DEFAULT NOW(),
	ALTER COLUMN created_at SET NOT NULL,
	ALTER COLUMN updated_at SET DEFAULT NOW(),
	ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE public.subject_modules
	ADD CONSTRAINT subject_modules_grade_check
	CHECK (grade >= 0 AND grade <= 10);

-- =====================================================
-- EQUIVALENCES NORMALIZATION
-- =====================================================
DO $$
DECLARE
	c RECORD;
BEGIN
	FOR c IN
		SELECT conname
		FROM pg_constraint
		WHERE conrelid = 'public.equivalences'::regclass
			AND contype = 'c'
			AND (
				pg_get_constraintdef(oid) ILIKE '%subject_a_id%'
				OR pg_get_constraintdef(oid) ILIKE '%subject_b_id%'
				OR pg_get_constraintdef(oid) ILIKE '%type%'
			)
	LOOP
		EXECUTE format('ALTER TABLE public.equivalences DROP CONSTRAINT %I', c.conname);
	END LOOP;
END $$;

UPDATE public.equivalences
SET type = 'total'
WHERE type IS NULL OR type NOT IN ('total', 'parcial');

UPDATE public.equivalences
SET created_at = COALESCE(created_at, NOW()),
		updated_at = COALESCE(updated_at, NOW());

ALTER TABLE public.equivalences
	ALTER COLUMN type SET NOT NULL,
	ALTER COLUMN created_at SET DEFAULT NOW(),
	ALTER COLUMN created_at SET NOT NULL,
	ALTER COLUMN updated_at SET DEFAULT NOW(),
	ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE public.equivalences
	ADD CONSTRAINT equivalences_type_check
	CHECK (type IN ('total', 'parcial')),
	ADD CONSTRAINT different_subjects
	CHECK (subject_a_id <> subject_b_id);

-- =====================================================
-- CREATE TABLES ADDED IN HUMANIS TARGET
-- =====================================================
CREATE TABLE IF NOT EXISTS public.subject_schedules (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	subject_id UUID NOT NULL REFERENCES public.subjects(id) ON DELETE CASCADE,
	day_of_week TEXT NOT NULL
		CHECK (day_of_week IN ('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO')),
	start_time TIME NOT NULL,
	end_time TIME NOT NULL,
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	CONSTRAINT valid_subject_schedule_time_range CHECK (end_time > start_time)
);

CREATE TABLE IF NOT EXISTS public.events (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
	title TEXT NOT NULL,
	color TEXT NOT NULL DEFAULT '#3B82F6',
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.event_schedules (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	event_id UUID NOT NULL REFERENCES public.events(id) ON DELETE CASCADE,
	start_time TIMESTAMPTZ NOT NULL,
	end_time TIMESTAMPTZ NOT NULL,
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	CONSTRAINT valid_event_time_range CHECK (end_time > start_time)
);

CREATE TABLE IF NOT EXISTS public.event_subjects (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	event_id UUID NOT NULL REFERENCES public.events(id) ON DELETE CASCADE,
	subject_id UUID NOT NULL REFERENCES public.subjects(id) ON DELETE CASCADE,
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	CONSTRAINT event_subjects_unique UNIQUE (event_id, subject_id)
);

CREATE TABLE IF NOT EXISTS public.reminders (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
	title TEXT NOT NULL,
	color TEXT NOT NULL DEFAULT '#3B82F6',
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.reminder_subjects (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	reminder_id UUID NOT NULL REFERENCES public.reminders(id) ON DELETE CASCADE,
	subject_id UUID NOT NULL REFERENCES public.subjects(id) ON DELETE CASCADE,
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	CONSTRAINT reminder_subjects_unique UNIQUE (reminder_id, subject_id)
);

CREATE TABLE IF NOT EXISTS public.user_settings (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	user_id UUID NOT NULL UNIQUE REFERENCES auth.users(id) ON DELETE CASCADE,
	theme TEXT NOT NULL DEFAULT 'SYSTEM' CHECK (theme IN ('DARK', 'LIGHT', 'SYSTEM')),
	notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
	time_format_24h BOOLEAN NOT NULL DEFAULT FALSE,
	planner_start_hour INTEGER NOT NULL DEFAULT 8 CHECK (planner_start_hour >= 0 AND planner_start_hour <= 23),
	planner_end_hour INTEGER NOT NULL DEFAULT 20 CHECK (planner_end_hour >= 0 AND planner_end_hour <= 23),
	week_start_day TEXT NOT NULL DEFAULT 'LUNES'
		CHECK (week_start_day IN ('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO')),
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	CONSTRAINT valid_planner_hours CHECK (planner_end_hour > planner_start_hour)
);

CREATE TABLE IF NOT EXISTS public.audit_logs (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
	action TEXT NOT NULL,
	table_name TEXT NOT NULL,
	record_id UUID,
	old_value JSONB,
	new_value JSONB,
	details TEXT,
	ip_address INET,
	user_agent TEXT,
	updated_by UUID REFERENCES auth.users(id),
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.support_tickets (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
	type TEXT NOT NULL CHECK (type IN ('REPORTE_PROBLEMA', 'SUGERENCIA')),
	title TEXT NOT NULL,
	message TEXT NOT NULL,
	status TEXT NOT NULL DEFAULT 'ABIERTO'
		CHECK (status IN ('ABIERTO', 'EN_PROCESO', 'RESUELTO', 'CERRADO')),
	created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
	updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- INDEXES
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_careers_user_id ON public.careers(user_id);
CREATE INDEX IF NOT EXISTS idx_careers_status ON public.careers(status);

CREATE INDEX IF NOT EXISTS idx_subjects_career_id ON public.subjects(career_id);
CREATE INDEX IF NOT EXISTS idx_subjects_code ON public.subjects(code) WHERE code IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_subjects_status ON public.subjects(status);
CREATE INDEX IF NOT EXISTS idx_subjects_grade ON public.subjects(grade) WHERE grade IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_subject_modules_subject_id ON public.subject_modules(subject_id);

CREATE INDEX IF NOT EXISTS idx_equivalences_user_id ON public.equivalences(user_id);
CREATE INDEX IF NOT EXISTS idx_equivalences_subject_a_id ON public.equivalences(subject_a_id);
CREATE INDEX IF NOT EXISTS idx_equivalences_subject_b_id ON public.equivalences(subject_b_id);

CREATE INDEX IF NOT EXISTS idx_subject_schedules_subject_id ON public.subject_schedules(subject_id);
CREATE INDEX IF NOT EXISTS idx_subject_schedules_day ON public.subject_schedules(day_of_week);

CREATE INDEX IF NOT EXISTS idx_events_user_id ON public.events(user_id);

CREATE INDEX IF NOT EXISTS idx_event_schedules_event_id ON public.event_schedules(event_id);
CREATE INDEX IF NOT EXISTS idx_event_schedules_start_time ON public.event_schedules(start_time);
CREATE INDEX IF NOT EXISTS idx_event_schedules_end_time ON public.event_schedules(end_time);

CREATE INDEX IF NOT EXISTS idx_event_subjects_event_id ON public.event_subjects(event_id);
CREATE INDEX IF NOT EXISTS idx_event_subjects_subject_id ON public.event_subjects(subject_id);

CREATE INDEX IF NOT EXISTS idx_reminders_user_id ON public.reminders(user_id);

CREATE INDEX IF NOT EXISTS idx_reminder_subjects_reminder_id ON public.reminder_subjects(reminder_id);
CREATE INDEX IF NOT EXISTS idx_reminder_subjects_subject_id ON public.reminder_subjects(subject_id);

CREATE INDEX IF NOT EXISTS idx_user_settings_user_id ON public.user_settings(user_id);
CREATE INDEX IF NOT EXISTS idx_user_settings_theme ON public.user_settings(theme);

CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON public.audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON public.audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_table_name ON public.audit_logs(table_name);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON public.audit_logs(created_at);

CREATE INDEX IF NOT EXISTS idx_support_tickets_user_id ON public.support_tickets(user_id);
CREATE INDEX IF NOT EXISTS idx_support_tickets_type ON public.support_tickets(type);
CREATE INDEX IF NOT EXISTS idx_support_tickets_status ON public.support_tickets(status);

-- =====================================================
-- TRIGGERS: updated_at
-- =====================================================
CREATE OR REPLACE FUNCTION public.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
	NEW.updated_at = NOW();
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_careers_updated_at ON public.careers;
CREATE TRIGGER update_careers_updated_at
	BEFORE UPDATE ON public.careers
	FOR EACH ROW
	EXECUTE FUNCTION public.update_updated_at_column();

DROP TRIGGER IF EXISTS update_subjects_updated_at ON public.subjects;
CREATE TRIGGER update_subjects_updated_at
	BEFORE UPDATE ON public.subjects
	FOR EACH ROW
	EXECUTE FUNCTION public.update_updated_at_column();

DROP TRIGGER IF EXISTS update_subject_modules_updated_at ON public.subject_modules;
CREATE TRIGGER update_subject_modules_updated_at
	BEFORE UPDATE ON public.subject_modules
	FOR EACH ROW
	EXECUTE FUNCTION public.update_updated_at_column();

DROP TRIGGER IF EXISTS update_equivalences_updated_at ON public.equivalences;
CREATE TRIGGER update_equivalences_updated_at
	BEFORE UPDATE ON public.equivalences
	FOR EACH ROW
	EXECUTE FUNCTION public.update_updated_at_column();

DROP TRIGGER IF EXISTS update_subject_schedules_updated_at ON public.subject_schedules;
CREATE TRIGGER update_subject_schedules_updated_at
	BEFORE UPDATE ON public.subject_schedules
	FOR EACH ROW
	EXECUTE FUNCTION public.update_updated_at_column();

DROP TRIGGER IF EXISTS update_events_updated_at ON public.events;
CREATE TRIGGER update_events_updated_at
	BEFORE UPDATE ON public.events
	FOR EACH ROW
	EXECUTE FUNCTION public.update_updated_at_column();

DROP TRIGGER IF EXISTS update_event_schedules_updated_at ON public.event_schedules;
CREATE TRIGGER update_event_schedules_updated_at
	BEFORE UPDATE ON public.event_schedules
	FOR EACH ROW
	EXECUTE FUNCTION public.update_updated_at_column();

DROP TRIGGER IF EXISTS update_reminders_updated_at ON public.reminders;
CREATE TRIGGER update_reminders_updated_at
	BEFORE UPDATE ON public.reminders
	FOR EACH ROW
	EXECUTE FUNCTION public.update_updated_at_column();

DROP TRIGGER IF EXISTS update_user_settings_updated_at ON public.user_settings;
CREATE TRIGGER update_user_settings_updated_at
	BEFORE UPDATE ON public.user_settings
	FOR EACH ROW
	EXECUTE FUNCTION public.update_updated_at_column();

DROP TRIGGER IF EXISTS update_support_tickets_updated_at ON public.support_tickets;
CREATE TRIGGER update_support_tickets_updated_at
	BEFORE UPDATE ON public.support_tickets
	FOR EACH ROW
	EXECUTE FUNCTION public.update_updated_at_column();

-- =====================================================
-- RLS
-- =====================================================
ALTER TABLE public.careers ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.subjects ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.subject_modules ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.equivalences ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.subject_schedules ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.events ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.event_schedules ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.event_subjects ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reminders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reminder_subjects ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_settings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.audit_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.support_tickets ENABLE ROW LEVEL SECURITY;

-- Drop all existing policies for target tables to avoid drift.
DO $$
DECLARE
	p RECORD;
BEGIN
	FOR p IN
		SELECT tablename, policyname
		FROM pg_policies
		WHERE schemaname = 'public'
			AND tablename IN (
				'careers', 'subjects', 'subject_modules', 'equivalences', 'subject_schedules',
				'events', 'event_schedules', 'event_subjects', 'reminders', 'reminder_subjects',
				'user_settings', 'audit_logs', 'support_tickets'
			)
	LOOP
		EXECUTE format('DROP POLICY IF EXISTS %I ON public.%I', p.policyname, p.tablename);
	END LOOP;
END $$;

-- careers
CREATE POLICY "Users can view their own careers"
	ON public.careers FOR SELECT
	USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own careers"
	ON public.careers FOR INSERT
	WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own careers"
	ON public.careers FOR UPDATE
	USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own careers"
	ON public.careers FOR DELETE
	USING (auth.uid() = user_id);

-- subjects
CREATE POLICY "Users can view subjects from their careers"
	ON public.subjects FOR SELECT
	USING (
		EXISTS (
			SELECT 1 FROM public.careers
			WHERE public.careers.id = public.subjects.career_id
			AND public.careers.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can insert subjects to their careers"
	ON public.subjects FOR INSERT
	WITH CHECK (
		EXISTS (
			SELECT 1 FROM public.careers
			WHERE public.careers.id = public.subjects.career_id
			AND public.careers.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can update subjects from their careers"
	ON public.subjects FOR UPDATE
	USING (
		EXISTS (
			SELECT 1 FROM public.careers
			WHERE public.careers.id = public.subjects.career_id
			AND public.careers.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can delete subjects from their careers"
	ON public.subjects FOR DELETE
	USING (
		EXISTS (
			SELECT 1 FROM public.careers
			WHERE public.careers.id = public.subjects.career_id
			AND public.careers.user_id = auth.uid()
		)
	);

-- subject_modules
CREATE POLICY "Users can view modules from their subjects"
	ON public.subject_modules FOR SELECT
	USING (
		EXISTS (
			SELECT 1 FROM public.subjects
			JOIN public.careers ON public.careers.id = public.subjects.career_id
			WHERE public.subjects.id = public.subject_modules.subject_id
			AND public.careers.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can insert modules to their subjects"
	ON public.subject_modules FOR INSERT
	WITH CHECK (
		EXISTS (
			SELECT 1 FROM public.subjects
			JOIN public.careers ON public.careers.id = public.subjects.career_id
			WHERE public.subjects.id = public.subject_modules.subject_id
			AND public.careers.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can update modules from their subjects"
	ON public.subject_modules FOR UPDATE
	USING (
		EXISTS (
			SELECT 1 FROM public.subjects
			JOIN public.careers ON public.careers.id = public.subjects.career_id
			WHERE public.subjects.id = public.subject_modules.subject_id
			AND public.careers.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can delete modules from their subjects"
	ON public.subject_modules FOR DELETE
	USING (
		EXISTS (
			SELECT 1 FROM public.subjects
			JOIN public.careers ON public.careers.id = public.subjects.career_id
			WHERE public.subjects.id = public.subject_modules.subject_id
			AND public.careers.user_id = auth.uid()
		)
	);

-- equivalences
CREATE POLICY "Users can view their own equivalences"
	ON public.equivalences FOR SELECT
	USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own equivalences"
	ON public.equivalences FOR INSERT
	WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own equivalences"
	ON public.equivalences FOR UPDATE
	USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own equivalences"
	ON public.equivalences FOR DELETE
	USING (auth.uid() = user_id);

-- subject_schedules
CREATE POLICY "Users can view schedules from their subjects"
	ON public.subject_schedules FOR SELECT
	USING (
		EXISTS (
			SELECT 1 FROM public.subjects
			JOIN public.careers ON public.careers.id = public.subjects.career_id
			WHERE public.subjects.id = public.subject_schedules.subject_id
			AND public.careers.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can insert schedules to their subjects"
	ON public.subject_schedules FOR INSERT
	WITH CHECK (
		EXISTS (
			SELECT 1 FROM public.subjects
			JOIN public.careers ON public.careers.id = public.subjects.career_id
			WHERE public.subjects.id = public.subject_schedules.subject_id
			AND public.careers.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can update schedules from their subjects"
	ON public.subject_schedules FOR UPDATE
	USING (
		EXISTS (
			SELECT 1 FROM public.subjects
			JOIN public.careers ON public.careers.id = public.subjects.career_id
			WHERE public.subjects.id = public.subject_schedules.subject_id
			AND public.careers.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can delete schedules from their subjects"
	ON public.subject_schedules FOR DELETE
	USING (
		EXISTS (
			SELECT 1 FROM public.subjects
			JOIN public.careers ON public.careers.id = public.subjects.career_id
			WHERE public.subjects.id = public.subject_schedules.subject_id
			AND public.careers.user_id = auth.uid()
		)
	);

-- events
CREATE POLICY "Users can view their own events"
	ON public.events FOR SELECT
	USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own events"
	ON public.events FOR INSERT
	WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own events"
	ON public.events FOR UPDATE
	USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own events"
	ON public.events FOR DELETE
	USING (auth.uid() = user_id);

-- event_schedules
CREATE POLICY "Users can view event schedules from their events"
	ON public.event_schedules FOR SELECT
	USING (
		EXISTS (
			SELECT 1 FROM public.events
			WHERE public.events.id = public.event_schedules.event_id
			AND public.events.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can insert event schedules to their events"
	ON public.event_schedules FOR INSERT
	WITH CHECK (
		EXISTS (
			SELECT 1 FROM public.events
			WHERE public.events.id = public.event_schedules.event_id
			AND public.events.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can update event schedules from their events"
	ON public.event_schedules FOR UPDATE
	USING (
		EXISTS (
			SELECT 1 FROM public.events
			WHERE public.events.id = public.event_schedules.event_id
			AND public.events.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can delete event schedules from their events"
	ON public.event_schedules FOR DELETE
	USING (
		EXISTS (
			SELECT 1 FROM public.events
			WHERE public.events.id = public.event_schedules.event_id
			AND public.events.user_id = auth.uid()
		)
	);

-- event_subjects
CREATE POLICY "Users can view event-subject relations"
	ON public.event_subjects FOR SELECT
	USING (
		EXISTS (
			SELECT 1 FROM public.events
			WHERE public.events.id = public.event_subjects.event_id
			AND public.events.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can insert event-subject relations"
	ON public.event_subjects FOR INSERT
	WITH CHECK (
		EXISTS (
			SELECT 1 FROM public.events
			WHERE public.events.id = public.event_subjects.event_id
			AND public.events.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can delete event-subject relations"
	ON public.event_subjects FOR DELETE
	USING (
		EXISTS (
			SELECT 1 FROM public.events
			WHERE public.events.id = public.event_subjects.event_id
			AND public.events.user_id = auth.uid()
		)
	);

-- reminders
CREATE POLICY "Users can view their own reminders"
	ON public.reminders FOR SELECT
	USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own reminders"
	ON public.reminders FOR INSERT
	WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own reminders"
	ON public.reminders FOR UPDATE
	USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own reminders"
	ON public.reminders FOR DELETE
	USING (auth.uid() = user_id);

-- reminder_subjects
CREATE POLICY "Users can view reminder-subject relations"
	ON public.reminder_subjects FOR SELECT
	USING (
		EXISTS (
			SELECT 1 FROM public.reminders
			WHERE public.reminders.id = public.reminder_subjects.reminder_id
			AND public.reminders.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can insert reminder-subject relations"
	ON public.reminder_subjects FOR INSERT
	WITH CHECK (
		EXISTS (
			SELECT 1 FROM public.reminders
			WHERE public.reminders.id = public.reminder_subjects.reminder_id
			AND public.reminders.user_id = auth.uid()
		)
	);

CREATE POLICY "Users can delete reminder-subject relations"
	ON public.reminder_subjects FOR DELETE
	USING (
		EXISTS (
			SELECT 1 FROM public.reminders
			WHERE public.reminders.id = public.reminder_subjects.reminder_id
			AND public.reminders.user_id = auth.uid()
		)
	);

-- user_settings
CREATE POLICY "Users can view their own settings"
	ON public.user_settings FOR SELECT
	USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own settings"
	ON public.user_settings FOR INSERT
	WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own settings"
	ON public.user_settings FOR UPDATE
	USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own settings"
	ON public.user_settings FOR DELETE
	USING (auth.uid() = user_id);

-- audit_logs
CREATE POLICY "Users can view their own audit logs"
	ON public.audit_logs FOR SELECT
	USING (auth.uid() = user_id);

CREATE POLICY "System can insert audit logs"
	ON public.audit_logs FOR INSERT
	WITH CHECK (true);

-- support_tickets
CREATE POLICY "Users can view their own support tickets"
	ON public.support_tickets FOR SELECT
	USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own support tickets"
	ON public.support_tickets FOR INSERT
	WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own support tickets"
	ON public.support_tickets FOR UPDATE
	USING (auth.uid() = user_id);

-- =====================================================
-- COMMENTS
-- =====================================================
COMMENT ON TABLE public.careers IS 'Carreras universitarias de los usuarios';
COMMENT ON TABLE public.subjects IS 'Materias de cada carrera con su estado y correlativas';
COMMENT ON TABLE public.subject_modules IS 'Modulos/parciales de materias que se evaluan por separado';
COMMENT ON TABLE public.equivalences IS 'Equivalencias entre materias de diferentes carreras';

COMMENT ON TABLE public.subject_schedules IS 'Horarios semanales recurrentes de cada materia';
COMMENT ON TABLE public.events IS 'Eventos del usuario (examenes, entregas, etc.)';
COMMENT ON TABLE public.event_schedules IS 'Horarios especificos de cada evento (fecha/hora exacta)';
COMMENT ON TABLE public.event_subjects IS 'Relacion muchos-a-muchos entre eventos y materias';
COMMENT ON TABLE public.reminders IS 'Recordatorios del usuario';
COMMENT ON TABLE public.reminder_subjects IS 'Relacion muchos-a-muchos entre recordatorios y materias';
COMMENT ON TABLE public.user_settings IS 'Configuracion personalizada de cada usuario';
COMMENT ON TABLE public.audit_logs IS 'Registro de auditoria de acciones del sistema';
COMMENT ON TABLE public.support_tickets IS 'Tickets de soporte (problemas reportados y sugerencias)';

COMMENT ON COLUMN public.subjects.correlatives IS 'Array de UUIDs de materias correlativas requeridas';
COMMENT ON COLUMN public.subjects.status IS 'Estado: pendiente, cursando, regular, aprobada, libre';
COMMENT ON COLUMN public.subjects.is_entrance_course IS 'Indica si es un curso de ingreso/nivelacion';
COMMENT ON COLUMN public.subjects.color IS 'Color en formato hex para visualizacion (#3B82F6)';
COMMENT ON COLUMN public.subjects.grade_required_for_promotion IS 'Nota minima requerida para promocion directa';
COMMENT ON COLUMN public.equivalences.type IS 'Tipo de equivalencia: total o parcial';
COMMENT ON COLUMN public.careers.has_hours IS 'Si esta carrera usa sistema de horas';
COMMENT ON COLUMN public.careers.has_credits IS 'Si esta carrera usa sistema de creditos';
COMMENT ON COLUMN public.careers.status IS 'Backend status: NOT_STARTED, IN_PROGRESS, PAUSED, COMPLETED';
COMMENT ON COLUMN public.subject_schedules.day_of_week IS 'Dia de la semana del horario recurrente';
COMMENT ON COLUMN public.event_schedules.start_time IS 'Fecha y hora especifica de inicio del evento';
COMMENT ON COLUMN public.user_settings.time_format_24h IS 'true=24h, false=12h';
COMMENT ON COLUMN public.audit_logs.old_value IS 'Valor anterior del registro (JSON)';
COMMENT ON COLUMN public.audit_logs.new_value IS 'Valor nuevo del registro (JSON)';
COMMENT ON COLUMN public.support_tickets.status IS 'Estado: ABIERTO, EN_PROCESO, RESUELTO, CERRADO';

COMMIT;
