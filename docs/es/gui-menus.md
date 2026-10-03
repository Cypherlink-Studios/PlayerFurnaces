---
title: Menús y Layouts GUI
description: Manual detallado para personalizar menus.yml, cuadrículas ASCII dinámicas, tipos de ranura, estados de hornos, custom model data y cabezas de jugador.
sidebar:
  order: 3
---

# Menús y Layouts GUI (`menus.yml`)

PlayerFurnaces cuenta con un motor modular y dinámico de diseño de interfaces visuales gestionado a través de `menus.yml`. Los administradores pueden ajustar el tamaño de los inventarios, reorganizar los botones mediante mapas de caracteres ASCII, modificar estados visuales (`smelting`, `idle`, `no_fuel`, `locked`) y aplicar texturas personalizadas con CustomModelData o cabezas de jugador.

---

## 🗺️ Conceptos del Motor de Layouts

Cada menú definido en `menus.yml` se compone de tres partes clave:
1. **`title`**: El título del inventario (admite etiquetas MiniMessage, códigos HEX `#RRGGBB` y códigos clásicos `&`).
2. **`layout`**: Una lista de cadenas donde cada línea representa una fila de **9 casillas** (hasta 6 filas para un total de 54 casillas). Cada carácter se vincula a una definición de la sección `legend`. Los espacios en blanco (`" "`) permanecen vacíos e interactuables.
3. **`legend`**: Un diccionario que define qué representa cada carácter del `layout` (material, nombre visible, lore, acción al hacer clic o ranura de horno).

---

## 🗂️ 1. Menú Principal de Hornos (`furnace_hub`)

La interfaz `furnace_hub` muestra la colección de hornos virtuales pertenecientes al jugador.

### Configuración por Defecto

```yaml
furnace_hub:
  title: "Your Virtual Furnaces"
  layout:
    - "XXXXXXXXX"
    - "X#######X"
    - "X#######X"
    - "X#######X"
    - "X#######X"
    - "XXXXXXXXX"
  
  legend:
    'X': 
      type: FILLER
      material: BLACK_STAINED_GLASS_PANE
      name: " "
    
    '#':
      type: FURNACE_SLOT
      states:
        smelting:
          material: BLAST_FURNACE
          name: "&aHorno {id} &7(Cocinando)"
          lore:
            - "&7Ítem: &f{item} &8x{amount}"
            - "&7Tiempo restante: &f{time}s"
            - ""
            - "&e¡Clic para abrir!"
        idle:
          material: FURNACE
          name: "&aHorno {id} &7(Inactivo)"
          lore:
            - "&7Este horno no está cocinando nada."
            - ""
            - "&e¡Clic para abrir!"
        no_fuel:
          material: FURNACE
          name: "&cHorno {id} &7(Sin Combustible)"
          lore:
            - "&7Este horno se ha quedado sin carbón."
            - ""
            - "&e¡Clic para abrir!"
        locked:
          material: RED_STAINED_GLASS_PANE
          name: "&cHorno {id} &7(Bloqueado)"
          lore:
            - "&7No tienes permiso para"
            - "&7utilizar este horno."
            - "&7(Requiere un rango superior)"
```

### Estados de Ranura de Horno (`type: FURNACE_SLOT` o `#`)
El plugin evalúa automáticamente el estado de cada horno y muestra dinámicamente la configuración correspondiente:

| Clave de Estado | Condición de Activación | Variables Disponibles |
| :--- | :--- | :--- |
| `smelting` | Cocinando activamente con combustible disponible. | `{id}`, `{item}`, `{amount}`, `{time}` |
| `idle` | Desbloqueado y disponible, pero la ranura de entrada está vacía o ya terminó de fundir. | `{id}` |
| `no_fuel` | Contiene ítems de entrada, pero no tiene combustible y la llama está apagada. | `{id}` |
| `locked` | El jugador no posee el permiso requerido (`playerfurnaces.furnace.<id>`) para esa ranura. | `{id}` |

---

## 🔥 2. Menú del Horno Individual (`furnace_view`)

La interfaz `furnace_view` es el panel de fundición activo que se abre al seleccionar un horno desbloqueado o mediante `/furnace <id>`.

### Configuración por Defecto

```yaml
furnace_view:
  title: "Furnace {id} - {status}"
  layout:
    - "XXXXXXXXX"
    - "XXIXXYYXX"
    - "XXFXPOYXX"
    - "XXSXCYYXX"
    - "XXXXBXXXX"
    
  legend:
    'X': 
      type: FILLER
      material: BLACK_STAINED_GLASS_PANE
      name: " "
    'Y': 
      type: FILLER
      material: GRAY_STAINED_GLASS_PANE
      name: " "
    'I': 
      type: INPUT
    'O': 
      type: OUTPUT
    'S':
      type: FUEL_SLOT
    'C': 
      type: COLLECT
      material: HOPPER
      name: "&aRecolectar Ítems"
      lore:
        - "&7Haz clic para enviar todo"
        - "&7a tu inventario."
    'P': 
      type: PROGRESS
      active_material: LIME_STAINED_GLASS_PANE
      active_name: "&aProgreso: &f{pct}%"
      waiting_material: RED_STAINED_GLASS_PANE
      waiting_name: "&7Esperando..."
    'F': 
      type: FUEL_INDICATOR
      active_material: FIRE_CHARGE
      active_name: "&6Combustible Activo"
      active_lore:
        - "&7Tiempo restante: &f{time}s"
      inactive_material: COAL
      inactive_name: "&7Sin Combustible"
    'B': 
      type: BACK
      material: ARROW
      name: "&cVolver"
```

### Tipos Funcionales de Ranura

| Tipo | Propósito y Comportamiento |
| :--- | :--- |
| `INPUT` | Ranura donde los jugadores colocan los materiales sin procesar. |
| `OUTPUT` | Ranura donde se acumulan los resultados fundidos. El shift-click recolecta de forma segura. |
| `FUEL_SLOT` | Ranura donde se inserta el combustible (carbón, varas de blaze, cubos o combustibles personalizados). |
| `COLLECT` | Botón interactivo que transfiere toda la producción acumulada al inventario del jugador de un solo clic. |
| `PROGRESS` | Barra visual de progreso que muestra el porcentaje de cocción mediante `{pct}%`. Alterna entre `active_*` y `waiting_*`. |
| `FUEL_INDICATOR` | Indicador visual de llama que refleja el tiempo restante con `{time}s`. Alterna entre `active_*` e `inactive_*`. |
| `BACK` | Botón para regresar al menú principal `furnace_hub`. |
| `FILLER` | Ítem decorativo de fondo. Cancela clics por completo para prevenir robos o inyecciones de ítems. |

---

## 🎨 Propiedades Avanzadas de Ítems

Puedes personalizar cualquier ítem o estado en la leyenda con texturas, modelos 3D o cabezas de jugador:

```yaml
'H':
  type: FILLER
  material: PLAYER_HEAD
  name: "&6Cofre Decorativo"
  # Nombre del jugador para obtener la cabeza:
  skull_owner: "MHF_Chest"
  # O textura Base64 directa:
  # skull_texture: "eyJ0ZXh0dXJlcyI6..."
  # CustomModelData para Resource Packs:
  custom_model_data: 1042
  lore:
    - "&7Ítem decorativo con textura personalizada"
```

* **`custom_model_data`** (o `custom-model-data`): Asigna un número entero para paquetes de texturas personalizados con modelos 3D.
* **`skull_owner`** (o `owner`): Obtiene la skin del jugador indicado en ítems de tipo `PLAYER_HEAD`.
* **`skull_texture`** (o `texture`): Aplica directamente una textura codificada en Base64 a cabezas personalizadas.
