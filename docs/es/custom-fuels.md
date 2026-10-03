---
title: Combustibles Personalizados
description: Guía para definir combustibles personalizados para hornos, tiempos de quemado, carga recursiva y prioridad de resolución.
sidebar:
  order: 6
---

# Guía de Combustibles Personalizados (`fuels/*.yml`)

PlayerFurnaces permite definir combustibles globales dentro de `plugins/PlayerFurnaces/fuels/`. Estos combustibles pueden ajustar la duración de quemado de materiales existentes, registrar nuevos ítems como combustible o integrarse con ítems creados por plugins externos.

---

## 📁 Estructura de Directorios y Subcarpetas

Al igual que el motor de recetas, la carpeta de combustibles admite escaneo recursivo (`Files.walk`), facilitando la organización:

```text
plugins/PlayerFurnaces/fuels/
├── hyper_coal.yml
├── magia/
│   ├── esencia_fuego.yml
│   └── aliento_dragon.yml
└── industria/
    └── uranio_enriquecido.yml
```

---

## 📄 Sintaxis de Configuración

Cada archivo `.yml` en `fuels/` puede definir uno o más identificadores de combustible:

```yaml
hyper_coal:
  # Nombre del material Bukkit O identificador con namespace:
  type: "COAL"
  # Duración en ticks del servidor que arde una unidad (20 ticks = 1 segundo):
  burn-time-ticks: 1000
```

### Ejemplo Avanzado con Ítem de Terceros

```yaml
nucleo_infernal:
  type: "craftorithm:infernal_core"
  burn-time-ticks: 24000 # Arde durante 20 minutos (funde 120 ítems)
```

### Desglose de Parámetros

| Parámetro | Tipo | Requerido | Descripción |
| :--- | :--- | :--- | :--- |
| `type` | String | Sí | Identificador del material o ítem. Puede ser un material Bukkit (ej. `COAL`, `BLAZE_ROD`), un namespace de ítem personalizado (ej. `craftorithm:infernal_core`, `executableitems:super_coal`) o coincidir mediante implementaciones registradas de `ItemProvider`. |
| `burn-time-ticks` | Integer | Sí | Duración total en ticks que arde una unidad de este combustible dentro de un horno virtual. |

---

## 🔄 Jerarquía y Prioridad de Combustibles

Cuando un jugador coloca un ítem en la ranura de combustible (Ranura 29), PlayerFurnaces determina su duración de quemado siguiendo esta jerarquía estricta:

```text
+-------------------------------------------------------------+
|             JERARQUÍA DE RESOLUCIÓN DE COMBUSTIBLE          |
+-------------------------------------------------------------+

  1. SOBREESCRITURA EN LA RECETA ACTIVA
     └── 'fuel.burn-time-ticks' definido en la receta en curso.
     └── Tiene prioridad absoluta si está configurado.
             │ (si no está definido)
             ▼
  2. DEFINICIÓN GLOBAL DE COMBUSTIBLE PERSONALIZADO
     └── Coincidencia de 'type' en los archivos de 'fuels/*.yml'.
     └── Tiene prioridad sobre las duraciones vanilla.
             │ (si no coincide ningún combustible personalizado)
             ▼
  3. DURACIÓN VANILLA DE BUKKIT
     └── Valores nativos de Minecraft (Carbón = 1600t, Lava = 20000t, etc.).
     └── Devuelve 0 ticks si el ítem no es combustible válido en Minecraft.
+-------------------------------------------------------------+
```

---

## ⏱️ Tabla de Referencia de Tiempos de Quemado

| Combustible Vanilla | Ticks de Quemado | Duración Real | Ítems Fundidos (a 200 ticks/ítem) |
| :--- | :--- | :--- | :--- |
| Herramientas de Madera | `200 ticks` | 10 segundos | 1 ítem |
| Carbón / Carbón Vegetal | `1,600 ticks` | 80 segundos | 8 ítems |
| Vara de Blaze | `2,400 ticks` | 120 segundos | 12 ítems |
| Bloque de Algas Secas | `4,000 ticks` | 200 segundos | 20 ítems |
| Bloque de Carbón | `16,000 ticks` | 800 segundos | 80 ítems |
| Cubo de Lava | `20,000 ticks` | 1,000 segundos | 100 ítems |
