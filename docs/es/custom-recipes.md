---
title: Recetas Personalizadas
description: Guía completa para configurar recetas personalizadas de fundición, escaneo recursivo de subdirectorios, metadatos de ítems, tiempos y anulación de recetas vanilla.
sidebar:
  order: 5
---

# Guía de Recetas Personalizadas (`recipes/*.yml`)

PlayerFurnaces incluye un motor modular y extensible de recetas de fundición que permite a los administradores definir recetas personalizadas, inyectar metadatos avanzados (PDC, CustomModelData, nombres y descripciones formateadas), organizar archivos en subcarpetas y sobreescribir o desactivar recetas vanilla de Minecraft.

---

## 📁 Estructura de Directorios y Carga Recursiva

Las recetas se cargan desde `plugins/PlayerFurnaces/recipes/`. A partir de la versión `v1.4.0`, PlayerFurnaces utiliza escaneo recursivo de directorios (`Files.walk`), permitiendo organizar archivos en subcarpetas temáticas:

```text
plugins/PlayerFurnaces/recipes/
├── default.yml
├── magia/
│   ├── runas.yml
│   └── pociones.yml
├── metalurgia/
│   ├── bronce.yml
│   └── acero.yml
└── craftorithm/
    └── recetas_importadas.yml
```

Cada archivo `.yml` puede contener una o varias definiciones de recetas con identificadores únicos.

---

## 📄 Ejemplo Completo de Receta

El siguiente ejemplo muestra la sintaxis completa y todos los campos disponibles:

```yaml
fundicion_rubi_mitico:
  # Requisitos del ítem de entrada (Ranura 11)
  input:
    id: ruby_ore
    material: REDSTONE_ORE
    name: "<red>Mineral de Rubí"
    lore:
      - ""
      - "<gray>Gema en bruto extraída de las profundidades."
    custom-model-data: 1005
    pdc:
      "mineria:tipo": "rubi"

  # Producto resultante (Ranura 15)
  result:
    id: "craftorithm:ruby_ingot"
    material: REDSTONE
    amount: 1
    name: "<gradient:#ff0055:#ffaa00><b>Lingote de Rubí Refinado</b></gradient>"
    lore:
      - "<gray>Irradia calor intenso."
    custom-model-data: 2001
    pdc:
      "calidad:nivel": "mitico"

  # Parámetros de cocción
  cook-time-ticks: 100 # Duración de fundición: 100 ticks = 5 segundos (20 ticks = 1s)
  experience: 3.5      # Puntos de experiencia otorgados al retirar el ítem

  # Restricción de combustible (Opcional)
  fuel:
    type: "hyper_coal"  # Combustible requerido (ID de combustible personalizado, material o namespace)
    burn-time-ticks: 800 # Sobreescribe la duración de quemado exclusivamente para esta receta
```

---

## ⚙️ Parámetros de Configuración

### 1. Especificación de Entrada (`input`)
Define las propiedades requeridas en el ítem colocado en la ranura de entrada:

| Campo | Tipo | Requerido | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | String | No | Identificador del ítem o proveedor con formato namespaced (ej. `craftorithm:ruby_ore`, `ei:fire_gem`). Si se indica namespace, la validación se delega al proveedor registrado. |
| `material` | Material | Sí* | Material Bukkit vanilla (ej. `REDSTONE_ORE`, `RAW_IRON`, `COPPER_ORE`). (*No es necesario si se utiliza un `id` con namespace reconocido). |
| `name` | String | No | Nombre formateado requerido en el ítem de entrada. Admite MiniMessage, HEX y `&`. |
| `lore` | List<String> | No | Líneas de descripción (lore) requeridas en el ítem. |
| `custom-model-data` | Integer | No | Valor entero de `CustomModelData` requerido. |
| `pdc` | Map<String, String> | No | Pares clave-valor requeridos dentro del `PersistentDataContainer` del ítem. |

### 2. Especificación del Resultado (`result`)
Define el ítem generado en la ranura de salida:

| Campo | Tipo | Requerido | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | String | No | Identificador de plugin externo con namespace (ej. `craftorithm:ruby_ingot`, `executableitems:gold_coin`). Resuelve el NBT/PDC original del plugin externo. |
| `material` | Material | Sí* | Material Bukkit del ítem generado. (*Obligatorio si no se indica un `id` de proveedor externo). |
| `amount` | Integer | No | Cantidad producida por cada ciclo completado (por defecto: `1`). |
| `name` | String | No | Nombre personalizado aplicado al ítem resultante. |
| `lore` | List<String> | No | Líneas de lore aplicadas al ítem resultante. |
| `custom-model-data` | Integer | No | CustomModelData aplicado al ítem de salida. |
| `pdc` | Map<String, String> | No | Etiquetas PDC inyectadas en el ítem de salida. |

### 3. Tiempos y Experiencia
* **`cook-time-ticks`**: Cantidad de ticks necesarios para fundir una unidad. (Por defecto: `200` ticks = 10 segundos).
* **`experience`**: Experiencia otorgada al jugador al recolectar el producto. (Por defecto: `0.0`).

### 4. Restricción de Combustible (`fuel`)
Permite exigir un combustible específico para procesar la receta:
* **`type`**: Identificador del combustible requerido. Puede ser un combustible personalizado (`hyper_coal`), un material vanilla (`BLAZE_ROD`) o un ítem con namespace (`craftorithm:hellfire_shard`).
* **`burn-time-ticks`**: (Opcional) Sobreescribe la duración de quemado del combustible únicamente mientras se procesa esta receta específica.

---

## 🚫 Sobreescritura y Desactivación de Recetas Vanilla

### 1. Sobreescribir Recetas Nativas de Minecraft
Cualquier receta en `recipes/` que coincida con un material de entrada vanilla (ej. `RAW_IRON`) tendrá prioridad sobre la receta nativa de Bukkit:

```yaml
hierro_rapido:
  input:
    material: RAW_IRON
  result:
    material: IRON_INGOT
    amount: 2          # ¡Produce el doble de hierro!
  cook-time-ticks: 60  # Cocina en 3 segundos en vez de 10
  experience: 1.0
```

### 2. Desactivar Recetas Vanilla Global o Selectivamente
En `config.yml`, los administradores pueden deshabilitar la fundición vanilla o bloquear materiales concretos:

```yaml
recipes:
  vanilla-smelting:
    # Cambiar a false para desactivar toda la fundición vanilla en hornos virtuales:
    enabled: true
    # Lista negra de materiales específicos:
    disabled-materials:
      - RAW_IRON
      - RAW_GOLD
      - ANCIENT_DEBRIS
```
