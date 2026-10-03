---
title: Configuración General
description: Guía completa para config.yml, messages.yml, motores de base de datos, límites, fundición vanilla y formatos de mensajes.
sidebar:
  order: 2
---

# Configuración General

PlayerFurnaces gestiona sus ajustes globales, conexiones a base de datos, límites de hornos por jugador y mensajes en dos archivos principales: `config.yml` y `messages.yml`.

---

## 📁 `config.yml`

El archivo `config.yml` controla el motor de almacenamiento de base de datos, los límites de hornos por jugador, la tasa de refresco de las interfaces y el comportamiento de las recetas vanilla de Minecraft.

### Archivo por Defecto (`config.yml`)

```yaml
database:
  # Motor de base de datos: SQLITE o H2
  type: SQLITE
  # Nombre del archivo dentro de plugins/PlayerFurnaces/
  file: furnaces.db

settings:
  # Cantidad predeterminada de hornos visibles en el Hub (1 a 54)
  default-furnace-count: 14
  # Límite máximo absoluto de hornos en el sistema
  max-furnace-count: 54
  # Frecuencia de refresco visual en ticks del servidor (20 ticks = 1 segundo)
  gui-refresh-ticks: 10

recipes:
  vanilla-smelting:
    # Cambiar a false para desactivar todas las recetas vanilla en hornos virtuales
    enabled: true
    # Lista de nombres de materiales vanilla bloqueados para fundir
    disabled-materials:
      - RAW_IRON
      - ANCIENT_DEBRIS
```

### Explicación de Parámetros

| Clave | Tipo | Valor por Defecto | Descripción |
| :--- | :--- | :--- | :--- |
| `database.type` | String | `SQLITE` | Motor de persistencia. Opciones soportadas: `SQLITE` (archivo local único) o `H2` (base de datos relacional de alta concurrencia). |
| `database.file` | String | `furnaces.db` | Nombre del archivo de base de datos dentro de `plugins/PlayerFurnaces/`. |
| `settings.default-furnace-count` | Integer | `14` | Cantidad de ranuras de horno mostradas en el menú Hub del jugador (hasta 54). |
| `settings.max-furnace-count` | Integer | `54` | Límite superior global de hornos permitidos por jugador en el sistema. |
| `settings.gui-refresh-ticks` | Integer | `10` | Frecuencia en ticks (10 ticks = 0.5s) con la que se actualizan las animaciones y barras de progreso en interfaces abiertas. |
| `recipes.vanilla-smelting.enabled` | Boolean | `true` | Si es `true`, las recetas estándar de Minecraft funcionan como alternativa cuando no coincide ninguna receta personalizada. |
| `recipes.vanilla-smelting.disabled-materials` | List<String> | `[]` | Lista de nombres de materiales Bukkit que no se pueden fundir aunque la fundición vanilla esté habilitada. |

> [!NOTE]
> La disposición visual de los menús y sus títulos se configuran de forma independiente en `menus.yml`. Consulta [Menús y Layouts GUI](gui-menus/) para más detalles.

---

## 💬 `messages.yml`

El archivo `messages.yml` controla todos los mensajes enviados a los jugadores, notificaciones administrativas y alertas de error.

### Estándares de Formato de Texto

PlayerFurnaces admite tres formatos complementarios en cualquier cadena o lista de texto:
1. **Etiquetas MiniMessage**: `<gradient:#ff5555:#ffaa00>PlayerFurnaces</gradient>`, `<red><b>¡Error!</b></red>`, `<hover:show_text:'Haz clic'>Texto</hover>`.
2. **Códigos HEX Directos**: `#FF5733Texto` o `&#FF5733Texto`.
3. **Códigos de Color Tradicionales**: Códigos estándar con ampersand (`&a`, `&e`, `&7`, `&l`, `&r`).

### Archivo por Defecto (`messages.yml`)

```yaml
prefix: "&8[&ePlayerFurnaces&8] "
no-permission: "&cNo tienes permiso para realizar esta acción."
no-furnace-permission: "&cNo tienes permiso para acceder al Horno #{id}."
furnace-not-found: "&cEl horno especificado no existe o no está disponible."
player-not-found: "&cEl jugador no fue encontrado o sus datos no han cargado."
player-offline: "&cEl jugador {player} no está conectado."
reload-success: "&a¡Configuración y mensajes recargados correctamente!"
collect-success: "&a¡Ítems procesados recolectados correctamente!"
only-players: "&cEste comando solo puede ser ejecutado por un jugador."
furnace-id-invalid: "&cEl ID del horno debe ser un número entre 1 y {max}."
usage-furnace: "&cUso: /furnace [id]"
usage-admin-view: "&cUso: /pfadmin view <jugador> [id]"
admin-furnace-id-number: "&cEl ID del horno debe ser un número entero."

admin-help:
  - "&e&lComandos Administrativos de PlayerFurnaces:"
  - "&f/pfadmin view <jugador> [id] &7- Inspeccionar el horno de un jugador"
  - "&f/pfadmin force-open <jugador> <id> &7- Forzar apertura de un horno"
  - "&f/pfadmin import <plugin> [--overwrite] &7- Importar recetas de un plugin externo"
  - "&f/pfadmin reload &7- Recargar configuración y recetas"

import-usage: "&cUso: /pfa import <plugin> [--overwrite|-f]"
import-plugin-not-found: "&cEl plugin '{plugin}' no está instalado, activado o no es compatible para importación."
import-success: "&aSe importaron exitosamente {imported} recetas de {plugin} ({skipped} omitidas)."

collection:
  success: "&a¡Ítems procesados recolectados correctamente!"
  partial: "&eSe recolectó producción parcial (tu inventario está lleno)."
```

### Variables Disponibles (Placeholders)

| Variable | Mensajes donde aplica | Descripción |
| :--- | :--- | :--- |
| `{id}` | `no-furnace-permission`, títulos de GUI | Identificador numérico del horno (ej. `1`, `2`). |
| `{player}` | `player-offline`, título del Hub | Nombre del jugador objetivo. |
| `{max}` | `furnace-id-invalid` | Límite máximo de hornos permitidos (`max-furnace-count`). |
| `{plugin}` | `import-plugin-not-found`, `import-success` | Nombre del plugin externo a importar. |
| `{imported}` | `import-success` | Cantidad de recetas importadas y guardadas. |
| `{skipped}` | `import-success` | Cantidad de recetas omitidas (ej. duplicados sin `--overwrite`). |
