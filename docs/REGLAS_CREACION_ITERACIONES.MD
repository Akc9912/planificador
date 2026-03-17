# Reglas para Crear Iteraciones de Documentacion

## Objetivo

Definir un estandar unico para crear la documentacion de cada iteracion de trabajo, de forma que cualquier colaborador nuevo pueda ejecutar tareas sin ambiguedad.

## Reglas base (obligatorias)

1. Cada iteracion debe tener un nombre claro y corto.
2. Se debe crear una carpeta con el nombre de la iteracion dentro de documentacion.
3. Dentro de la carpeta de iteracion debe existir una subcarpeta por cada fase.
4. Dentro de cada fase debe existir un README.md de fase con links a sus tareas y checklist de avance.
5. Dentro de cada fase debe existir un archivo md por cada tarea de esa fase.
6. El README de la iteracion debe incluir links a cada fase con check de estado ([x] o [ ]).
7. El README de la iteracion debe incluir un desglose por fase (rango de tareas) con links al README de cada fase.
8. El nombre de cada archivo de tarea debe ser el identificador exacto de la tarea, por ejemplo: 1.1.md, 1.2.md, 2.1.md.
9. Cada tarea debe explicar con detalle que se debe hacer y por que se hace.
10. El nivel de detalle debe ser suficiente para que un junior, trainee o nuevo colaborador pueda ejecutar la tarea sin depender de contexto oral.
11. No se permiten descripciones vagas. Toda accion debe tener pasos verificables.

## Convencion de nombres

### Carpeta de iteracion

Formato recomendado:

- iteracion-XX-tema-principal

Donde:

- XX es un numero de dos digitos: 01, 02, 03.
- tema-principal usa minusculas y guiones medios.
- No usar espacios ni caracteres especiales.

Ejemplo:

- iteracion-01-monorepo-vite-springboot

### Carpeta de fase

Formato recomendado:

- fase-N-descripcion-corta

Ejemplo:

- fase-1-preparacion-y-setup-monorepo

### Archivo de tarea

Formato obligatorio:

- N.M.md

Ejemplo:

- 1.1.md
- 1.10.md
- 4.3.md

## Estructura minima obligatoria de cada iteracion

- README.md en la raiz de la iteracion.
- Una subcarpeta por fase.
- README.md dentro de cada fase.
- Un archivo md por tarea dentro de cada fase.

## Contenido obligatorio de cada archivo de tarea

Cada archivo de tarea debe incluir, en este orden cuando aplique:

1. Titulo de tarea (con numero y nombre).
2. Objetivo.
3. Comandos exactos (copiar y pegar).
4. Ejemplo de codigo (si la tarea cambia codigo).
5. Verificacion (como comprobar que salio bien).
6. Resultado esperado (que debe verse al finalizar).
7. Criterio de cierre (checklist).

## Estandar de redaccion

1. Escribir en lenguaje directo y tecnico simple.
2. Explicar una accion por paso.
3. Evitar palabras ambiguas como ajustar, revisar, configurar sin comando o criterio.
4. Incluir rutas, comandos y salidas esperadas siempre que sea posible.
5. Si existe riesgo conocido, dejar una nota de troubleshooting breve.

## Regla de calidad

Una tarea no se considera completa si falta cualquiera de estos puntos:

- Razon tecnica clara.
- Ejecucion reproducible.
- Verificacion objetiva.
- Evidencia de cierre.

## Proceso estandar para crear una nueva iteracion

1. Definir nombre de iteracion y alcance.
2. Extraer de la fuente de plan (por ejemplo MIGRATION_PLAN.md) la lista exacta de fases y tareas.
3. Crear carpeta de iteracion con su README (plan de esa iteracion).
4. Crear carpetas por fase.
5. Crear README.md en cada fase con links a tareas y checklist.
6. Crear archivos de tarea por numeracion exacta.
7. Completar cada archivo con el contenido obligatorio.
8. Revisar consistencia de numeracion y cobertura total.
9. Validar que no falte ninguna tarea del plan.

## Checklist final de validacion de una iteracion

- [ ] Existe carpeta de iteracion con nombre valido.
- [ ] Existe README en la iteracion.
- [ ] Existe una carpeta por fase.
- [ ] Existe README por fase con links a tareas.
- [ ] El README de la iteracion tiene links a fases con check de estado.
- [ ] Existe un archivo md por cada tarea planificada.
- [ ] La numeracion de archivos coincide con el plan.
- [ ] Todos los archivos de tarea tienen comandos o verificacion explicita.
- [ ] El nivel de detalle permite ejecucion por un colaborador nuevo.
