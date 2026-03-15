-- =====================================================
-- Humanis / miCarrera - Single DB Init Script
-- =====================================================
-- Purpose:
--   Single source-of-truth script to reset and recreate the backend schema.
--
-- IMPORTANT:
--   This script is DESTRUCTIVE. It drops all planner tables first.
--   Run only in environments where data loss is acceptable.
--
-- Target:
--   Supabase PostgreSQL (uses auth.users and auth.uid()).

BEGIN;

-- =====================================================
-- EXTENSIONS
-- =====================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- CLEAN RESET (DROP IN DEPENDENCY ORDER)
-- =====================================================
DROP TABLE IF EXISTS public.support_tickets CASCADE;
DROP TABLE IF EXISTS public.audit_logs CASCADE;
DROP TABLE IF EXISTS public.user_settings CASCADE;
DROP TABLE IF EXISTS public.reminder_subjects CASCADE;
DROP TABLE IF EXISTS public.reminders CASCADE;
DROP TABLE IF EXISTS public.event_subjects CASCADE;
DROP TABLE IF EXISTS public.event_schedules CASCADE;
DROP TABLE IF EXISTS public.events CASCADE;
DROP TABLE IF EXISTS public.subject_schedules CASCADE;
DROP TABLE IF EXISTS public.equivalences CASCADE;
DROP TABLE IF EXISTS public.subject_modules CASCADE;
DROP TABLE IF EXISTS public.subjects CASCADE;
DROP TABLE IF EXISTS public.careers CASCADE;

DROP FUNCTION IF EXISTS public.update_updated_at_column() CASCADE;

-- =====================================================
-- TABLE: careers
-- =====================================================
CREATE TABLE public.careers (
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

-- =====================================================
-- TABLE: subjects
-- =====================================================
CREATE TABLE public.subjects (
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

-- =====================================================
-- TABLE: subject_modules
-- =====================================================
CREATE TABLE public.subject_modules (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  subject_id UUID NOT NULL REFERENCES public.subjects(id) ON DELETE CASCADE,
  name TEXT NOT NULL,
  grade NUMERIC(4,2) CHECK (grade >= 0 AND grade <= 10),
  module_order INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- TABLE: equivalences
-- =====================================================
CREATE TABLE public.equivalences (
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
-- TABLE: subject_schedules
-- =====================================================
CREATE TABLE public.subject_schedules (
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

-- =====================================================
-- TABLE: events
-- =====================================================
CREATE TABLE public.events (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  title TEXT NOT NULL,
  color TEXT NOT NULL DEFAULT '#3B82F6',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- TABLE: event_schedules
-- =====================================================
CREATE TABLE public.event_schedules (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  event_id UUID NOT NULL REFERENCES public.events(id) ON DELETE CASCADE,
  start_time TIMESTAMPTZ NOT NULL,
  end_time TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT valid_event_time_range CHECK (end_time > start_time)
);

-- =====================================================
-- TABLE: event_subjects
-- =====================================================
CREATE TABLE public.event_subjects (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  event_id UUID NOT NULL REFERENCES public.events(id) ON DELETE CASCADE,
  subject_id UUID NOT NULL REFERENCES public.subjects(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT event_subjects_unique UNIQUE (event_id, subject_id)
);

-- =====================================================
-- TABLE: reminders
-- =====================================================
CREATE TABLE public.reminders (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  title TEXT NOT NULL,
  color TEXT NOT NULL DEFAULT '#3B82F6',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- TABLE: reminder_subjects
-- =====================================================
CREATE TABLE public.reminder_subjects (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  reminder_id UUID NOT NULL REFERENCES public.reminders(id) ON DELETE CASCADE,
  subject_id UUID NOT NULL REFERENCES public.subjects(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT reminder_subjects_unique UNIQUE (reminder_id, subject_id)
);

-- =====================================================
-- TABLE: user_settings
-- =====================================================
CREATE TABLE public.user_settings (
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

-- =====================================================
-- TABLE: audit_logs
-- =====================================================
CREATE TABLE public.audit_logs (
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

-- =====================================================
-- TABLE: support_tickets
-- =====================================================
CREATE TABLE public.support_tickets (
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
CREATE INDEX idx_careers_user_id ON public.careers(user_id);
CREATE INDEX idx_careers_status ON public.careers(status);

CREATE INDEX idx_subjects_career_id ON public.subjects(career_id);
CREATE INDEX idx_subjects_code ON public.subjects(code) WHERE code IS NOT NULL;
CREATE INDEX idx_subjects_status ON public.subjects(status);
CREATE INDEX idx_subjects_grade ON public.subjects(grade) WHERE grade IS NOT NULL;

CREATE INDEX idx_subject_modules_subject_id ON public.subject_modules(subject_id);

CREATE INDEX idx_equivalences_user_id ON public.equivalences(user_id);
CREATE INDEX idx_equivalences_subject_a_id ON public.equivalences(subject_a_id);
CREATE INDEX idx_equivalences_subject_b_id ON public.equivalences(subject_b_id);

CREATE INDEX idx_subject_schedules_subject_id ON public.subject_schedules(subject_id);
CREATE INDEX idx_subject_schedules_day ON public.subject_schedules(day_of_week);

CREATE INDEX idx_events_user_id ON public.events(user_id);

CREATE INDEX idx_event_schedules_event_id ON public.event_schedules(event_id);
CREATE INDEX idx_event_schedules_start_time ON public.event_schedules(start_time);
CREATE INDEX idx_event_schedules_end_time ON public.event_schedules(end_time);

CREATE INDEX idx_event_subjects_event_id ON public.event_subjects(event_id);
CREATE INDEX idx_event_subjects_subject_id ON public.event_subjects(subject_id);

CREATE INDEX idx_reminders_user_id ON public.reminders(user_id);

CREATE INDEX idx_reminder_subjects_reminder_id ON public.reminder_subjects(reminder_id);
CREATE INDEX idx_reminder_subjects_subject_id ON public.reminder_subjects(subject_id);

CREATE INDEX idx_user_settings_user_id ON public.user_settings(user_id);
CREATE INDEX idx_user_settings_theme ON public.user_settings(theme);

CREATE INDEX idx_audit_logs_user_id ON public.audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON public.audit_logs(action);
CREATE INDEX idx_audit_logs_table_name ON public.audit_logs(table_name);
CREATE INDEX idx_audit_logs_created_at ON public.audit_logs(created_at);

CREATE INDEX idx_support_tickets_user_id ON public.support_tickets(user_id);
CREATE INDEX idx_support_tickets_type ON public.support_tickets(type);
CREATE INDEX idx_support_tickets_status ON public.support_tickets(status);

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

CREATE TRIGGER update_careers_updated_at
  BEFORE UPDATE ON public.careers
  FOR EACH ROW
  EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_subjects_updated_at
  BEFORE UPDATE ON public.subjects
  FOR EACH ROW
  EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_subject_modules_updated_at
  BEFORE UPDATE ON public.subject_modules
  FOR EACH ROW
  EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_equivalences_updated_at
  BEFORE UPDATE ON public.equivalences
  FOR EACH ROW
  EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_subject_schedules_updated_at
  BEFORE UPDATE ON public.subject_schedules
  FOR EACH ROW
  EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_events_updated_at
  BEFORE UPDATE ON public.events
  FOR EACH ROW
  EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_event_schedules_updated_at
  BEFORE UPDATE ON public.event_schedules
  FOR EACH ROW
  EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_reminders_updated_at
  BEFORE UPDATE ON public.reminders
  FOR EACH ROW
  EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_user_settings_updated_at
  BEFORE UPDATE ON public.user_settings
  FOR EACH ROW
  EXECUTE FUNCTION public.update_updated_at_column();

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
