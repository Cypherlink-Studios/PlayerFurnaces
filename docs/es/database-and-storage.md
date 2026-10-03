---
title: Base de Datos y Persistencia
description: Guía completa sobre los motores SQLite y H2, pool de conexiones HikariCP, serialización binaria NBT y garantías contra duplicaciones.
sidebar:
  order: 8
---

# Base de Datos y Arquitectura de Persistencia

PlayerFurnaces ha sido diseñado para garantizar cero pérdida de ítems, prevenir duplicaciones y preservar intactos los datos y metadatos NBT/PDC a través de reinicios del servidor y desconexiones de jugadores.

---

## 🗄️ Motores de Base de Datos Soportados

El motor de almacenamiento se configura en la sección `database` de `config.yml`:

```yaml
database:
  # Opciones disponibles: SQLITE, H2
  type: SQLITE
  file: furnaces.db
```

### 1. SQLite (`type: SQLITE`)
* **Recomendado para**: Servidores pequeños o medianos, modalidades survival clásicas o entornos de prueba locales.
* **Almacenamiento**: Archivo local único en `plugins/PlayerFurnaces/furnaces.db`.
* **Ventajas**: No requiere configuración externa. Ligero, fiable y fácil de respaldar mediante copia directa del archivo `.db`.

### 2. Motor de Base de Datos H2 (`type: H2`)
* **Recomendado para**: Servidores con alto tráfico y cientos de hornos cocinando simultáneamente.
* **Almacenamiento**: Base de datos relacional embebida con tablas indexadas.
* **Ventajas**: Mayor rendimiento en escritura concurrente y menor latencia bajo operaciones simultáneas en segundo plano.

---

## ⚡ Pool de Conexiones (HikariCP)

Toda la comunicación con la base de datos se canaliza a través de **HikariCP**, un pool JDBC de alto rendimiento.
* **Operaciones Asíncronas**: Las tareas de guardado de estados, persistencia de ítems y carga de perfiles se ejecutan en hilos de trabajo independientes (`CompletableFuture`).
* **Cero Impacto en TPS**: El hilo principal del servidor de Minecraft no sufre bloqueos ni caídas de TPS durante el guardado intensivo.

---

## 📦 Serialización Binaria NBT (`byte[] BLOB`)

Muchos plugins convierten los ítems en cadenas de texto YAML o JSON, lo que con frecuencia descarta etiquetas NBT avanzadas, corrompe caracteres o pierde metadatos. PlayerFurnaces utiliza **arreglos de bytes binarios nativos de Bukkit**:

```text
+-------------------------------------------------------------+
|              CANAL DE PERSISTENCIA BINARIA DE ÍTEMS         |
+-------------------------------------------------------------+

  1. ITEMSTACK EN EL JUEGO
     └── Contiene lore, encantamientos, CustomModelData, PDC
             │
             ▼
  2. CODIFICACIÓN BINARIA NATIVA DE BUKKIT
     └── Serializado directamente a un BLOB comprimido (byte[])
             │
             ▼
  3. POOL ASÍNCRONO HIKARICP
     └── Enviado a los hilos de trabajo de SQLite/H2
             │
             ▼
  4. ALMACENAMIENTO SQL PERSISTENTE
     └── Guardado en la tabla 'player_furnaces' de forma atómica
+-------------------------------------------------------------+
```

### Garantías Fundamentales:
1. **Preservación Total de Metadatos**: Todos los valores de `CustomModelData`, descripciones personalizadas, nombres con formato RGB/HEX, encantamientos, durabilidad y etiquetas `PersistentDataContainer` (PDC) de plugins como **Craftorithm**, **ExecutableItems**, **Oraxen** e **ItemsAdder** permanecen intactos.
2. **Seguridad Transaccional Antiduplicaciones**: Las interacciones de shift-click y movimientos de cursor se gestionan de forma atómica. El plugin bloquea la recolección por doble clic y el intercambio rápido de teclas numéricas de la barra de acceso rápido para impedir inyecciones ilegales de ítems en ranuras protegidas.
3. **Sincronización Fuera de Línea mediante Delta-Time**: Aunque el servidor se apague durante días, al reconectar se calcula la diferencia de tiempo transcurrido y se actualizan los ítems fundidos con precisión milimétrica sin perder combustible.
