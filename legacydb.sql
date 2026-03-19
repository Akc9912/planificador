-- =====================================================
-- MiCarrera - Database Schema Completo
-- =====================================================
-- Script de creación completa de base de datos
-- Incluye todas las tablas, políticas RLS, triggers e índices

-- =====================================================
-- EXTENSIONES
-- =====================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- TABLA: careers (Carreras)
-- =====================================================
CREATE TABLE IF NOT EXISTS careers (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  name TEXT NOT NULL,
  institution TEXT NOT NULL,
  status TEXT NOT NULL CHECK (status IN ('no_iniciada', 'en_curso', 'pausada', 'finalizada')),
  start_date TEXT,
  has_hours BOOLEAN DEFAULT FALSE,
  has_credits BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_careers_user_id ON careers(user_id);
CREATE INDEX IF NOT EXISTS idx_careers_status ON careers(status);

-- =====================================================
-- TABLA: subjects (Materias)
-- =====================================================
CREATE TABLE IF NOT EXISTS subjects (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  career_id UUID NOT NULL REFERENCES careers(id) ON DELETE CASCADE,
  name TEXT NOT NULL,
  code TEXT,
  status TEXT NOT NULL CHECK (status IN ('pendiente', 'cursando', 'regular', 'aprobada', 'libre')),
  grade INTEGER CHECK (grade >= 0 AND grade <= 10),
  approval_method TEXT CHECK (approval_method IN ('promocion', 'examen_final', 'examen_libre')),
  correlatives TEXT[] DEFAULT '{}',
  year INTEGER CHECK (year >= 0),
  semester INTEGER CHECK (semester >= 0),
  is_entrance_course BOOLEAN DEFAULT FALSE,
  hours INTEGER CHECK (hours >= 0),
  credits INTEGER CHECK (credits >= 0),
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_subjects_career_id ON subjects(career_id);
CREATE INDEX IF NOT EXISTS idx_subjects_code ON subjects(code) WHERE code IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_subjects_status ON subjects(status);

-- =====================================================
-- TABLA: subject_modules (Módulos de Materias)
-- =====================================================
CREATE TABLE IF NOT EXISTS subject_modules (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  subject_id UUID NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
  name TEXT NOT NULL,
  grade NUMERIC(4,2) CHECK (grade >= 0 AND grade <= 10),
  module_order INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_subject_modules_subject_id ON subject_modules(subject_id);

-- =====================================================
-- TABLA: equivalences (Equivalencias entre materias)
-- =====================================================
CREATE TABLE IF NOT EXISTS equivalences (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  type TEXT NOT NULL CHECK (type IN ('total', 'parcial')),
  subject_a_id UUID NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
  subject_b_id UUID NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
  notes TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW(),
  CONSTRAINT different_subjects CHECK (subject_a_id != subject_b_id)
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_equivalences_user_id ON equivalences(user_id);
CREATE INDEX IF NOT EXISTS idx_equivalences_subject_a_id ON equivalences(subject_a_id);
CREATE INDEX IF NOT EXISTS idx_equivalences_subject_b_id ON equivalences(subject_b_id);

-- =====================================================
-- FUNCIONES: Triggers
-- =====================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers para actualizar updated_at
DROP TRIGGER IF EXISTS update_careers_updated_at ON careers;
CREATE TRIGGER update_careers_updated_at
  BEFORE UPDATE ON careers
  FOR EACH ROW
  EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_subjects_updated_at ON subjects;
CREATE TRIGGER update_subjects_updated_at
  BEFORE UPDATE ON subjects
  FOR EACH ROW
  EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_subject_modules_updated_at ON subject_modules;
CREATE TRIGGER update_subject_modules_updated_at
  BEFORE UPDATE ON subject_modules
  FOR EACH ROW
  EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_equivalences_updated_at ON equivalences;
CREATE TRIGGER update_equivalences_updated_at
  BEFORE UPDATE ON equivalences
  FOR EACH ROW
  EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- ROW LEVEL SECURITY (RLS)
-- =====================================================

-- Habilitar RLS en todas las tablas
ALTER TABLE careers ENABLE ROW LEVEL SECURITY;
ALTER TABLE subjects ENABLE ROW LEVEL SECURITY;
ALTER TABLE subject_modules ENABLE ROW LEVEL SECURITY;
ALTER TABLE equivalences ENABLE ROW LEVEL SECURITY;

-- =====================================================
-- POLÍTICAS RLS: careers
-- =====================================================
DROP POLICY IF EXISTS "Users can view their own careers" ON careers;
CREATE POLICY "Users can view their own careers"
  ON careers FOR SELECT
  USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Users can insert their own careers" ON careers;
CREATE POLICY "Users can insert their own careers"
  ON careers FOR INSERT
  WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS "Users can update their own careers" ON careers;
CREATE POLICY "Users can update their own careers"
  ON careers FOR UPDATE
  USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Users can delete their own careers" ON careers;
CREATE POLICY "Users can delete their own careers"
  ON careers FOR DELETE
  USING (auth.uid() = user_id);

-- =====================================================
-- POLÍTICAS RLS: subjects
-- =====================================================
DROP POLICY IF EXISTS "Users can view subjects from their careers" ON subjects;
CREATE POLICY "Users can view subjects from their careers"
  ON subjects FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM careers
      WHERE careers.id = subjects.career_id
      AND careers.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "Users can insert subjects to their careers" ON subjects;
CREATE POLICY "Users can insert subjects to their careers"
  ON subjects FOR INSERT
  WITH CHECK (
    EXISTS (
      SELECT 1 FROM careers
      WHERE careers.id = subjects.career_id
      AND careers.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "Users can update subjects from their careers" ON subjects;
CREATE POLICY "Users can update subjects from their careers"
  ON subjects FOR UPDATE
  USING (
    EXISTS (
      SELECT 1 FROM careers
      WHERE careers.id = subjects.career_id
      AND careers.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "Users can delete subjects from their careers" ON subjects;
CREATE POLICY "Users can delete subjects from their careers"
  ON subjects FOR DELETE
  USING (
    EXISTS (
      SELECT 1 FROM careers
      WHERE careers.id = subjects.career_id
      AND careers.user_id = auth.uid()
    )
  );

-- =====================================================
-- POLÍTICAS RLS: subject_modules
-- =====================================================
DROP POLICY IF EXISTS "Users can view modules from their subjects" ON subject_modules;
CREATE POLICY "Users can view modules from their subjects"
  ON subject_modules FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM subjects
      JOIN careers ON careers.id = subjects.career_id
      WHERE subjects.id = subject_modules.subject_id
      AND careers.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "Users can insert modules to their subjects" ON subject_modules;
CREATE POLICY "Users can insert modules to their subjects"
  ON subject_modules FOR INSERT
  WITH CHECK (
    EXISTS (
      SELECT 1 FROM subjects
      JOIN careers ON careers.id = subjects.career_id
      WHERE subjects.id = subject_modules.subject_id
      AND careers.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "Users can update modules from their subjects" ON subject_modules;
CREATE POLICY "Users can update modules from their subjects"
  ON subject_modules FOR UPDATE
  USING (
    EXISTS (
      SELECT 1 FROM subjects
      JOIN careers ON careers.id = subjects.career_id
      WHERE subjects.id = subject_modules.subject_id
      AND careers.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "Users can delete modules from their subjects" ON subject_modules;
CREATE POLICY "Users can delete modules from their subjects"
  ON subject_modules FOR DELETE
  USING (
    EXISTS (
      SELECT 1 FROM subjects
      JOIN careers ON careers.id = subjects.career_id
      WHERE subjects.id = subject_modules.subject_id
      AND careers.user_id = auth.uid()
    )
  );

-- =====================================================
-- POLÍTICAS RLS: equivalences
-- =====================================================
DROP POLICY IF EXISTS "Users can view their own equivalences" ON equivalences;
CREATE POLICY "Users can view their own equivalences"
  ON equivalences FOR SELECT
  USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Users can insert their own equivalences" ON equivalences;
CREATE POLICY "Users can insert their own equivalences"
  ON equivalences FOR INSERT
  WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS "Users can update their own equivalences" ON equivalences;
CREATE POLICY "Users can update their own equivalences"
  ON equivalences FOR UPDATE
  USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Users can delete their own equivalences" ON equivalences;
CREATE POLICY "Users can delete their own equivalences"
  ON equivalences FOR DELETE
  USING (auth.uid() = user_id);

-- =====================================================
-- COMENTARIOS
-- =====================================================
COMMENT ON TABLE careers IS 'Carreras universitarias de los usuarios';
COMMENT ON TABLE subjects IS 'Materias de cada carrera con su estado y correlativas';
COMMENT ON TABLE subject_modules IS 'Módulos/parciales de materias que se evalúan por separado';
COMMENT ON TABLE equivalences IS 'Equivalencias entre materias de diferentes carreras';

COMMENT ON COLUMN subjects.correlatives IS 'Array de IDs de materias correlativas requeridas';
COMMENT ON COLUMN subjects.status IS 'Estado: pendiente, cursando, regular, aprobada, libre';
COMMENT ON COLUMN subjects.is_entrance_course IS 'Indica si es un curso de ingreso/nivelación';
COMMENT ON COLUMN subjects.hours IS 'Horas totales de la materia (si la carrera usa horas)';
COMMENT ON COLUMN subjects.credits IS 'Créditos de la materia (si la carrera usa créditos)';
COMMENT ON COLUMN equivalences.type IS 'Tipo de equivalencia: total (mismo puntaje) o parcial';
COMMENT ON COLUMN careers.has_hours IS 'Si esta carrera usa sistema de horas';
COMMENT ON COLUMN careers.has_credits IS 'Si esta carrera usa sistema de créditos';

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
-- Para ejecutar este script:
-- 1. Ve a Supabase Dashboard → SQL Editor
-- 2. Copia y pega todo este contenido
-- 3. Ejecuta (Run)
-- 
-- El script es idempotente (puede ejecutarse múltiples veces sin errores)
-- gracias al uso de IF NOT EXISTS y DROP ... IF EXISTS
