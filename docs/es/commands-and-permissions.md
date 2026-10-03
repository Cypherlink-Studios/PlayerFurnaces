---
title: Comandos y Permisos
description: Referencia exhaustiva de comandos de usuario, inspección de administradores, apertura remota, importación de recetas y permisos.
sidebar:
  order: 4
---

# Comandos y Permisos

PlayerFurnaces ofrece comandos para jugadores con autocompletado contextual mediante Tab, potentes herramientas administrativas para inspeccionar y gestionar hornos, y una estructura granular de nodos de permiso.

---

## 🎮 Comandos de Jugador

### `/furnace`
* **Alias**: `/furnaces`, `/horno`, `/hornos`, `/pf`
* **Permiso**: `playerfurnaces.command.use` (por defecto: `true`)
* **Descripción**: Abre el panel principal (**Virtual Furnace Hub**) con todos los hornos accesibles del jugador.
* **Uso**:
  ```bash
  # Abre el menú principal con todos tus hornos
  /furnace

  # Abre directamente el horno número 1
  /furnace 1
  ```
* **Autocompletado Contextual**: Sugiere los identificadores numéricos de los hornos según los permisos asignados a cada usuario.

---

## 👮 Comandos de Administrador

### `/playerfurnacesadmin`
* **Alias**: `/pfadmin`, `/furnacesadmin`, `/pfa`
* **Permiso**: `playerfurnaces.admin` (por defecto: `op`)
* **Descripción**: Comando raíz para inspección, administración y mantenimiento del sistema.

### Subcomandos

#### 1. `/pfadmin view <jugador> [id]`
* **Descripción**: Permite a un administrador inspeccionar el Hub de hornos o abrir un horno individual de cualquier jugador, se encuentre en línea o desconectado.
* **Ejemplos**:
  ```bash
  # Inspeccionar el Hub de Steve
  /pfadmin view steve

  # Abrir el horno #3 de Steve directamente
  /pfadmin view steve 3
  ```

#### 2. `/pfadmin force-open <jugador> <id> [--bypass-perms|-b]`
* **Descripción**: Fuerza a un jugador conectado a abrir de inmediato la interfaz de su horno virtual especificado.
* **Comprobación de Permisos**: Por defecto, verifica si el jugador objetivo tiene permiso para usar ese horno (`playerfurnaces.furnace.<id>`). Si no lo posee, cancela la acción y avisa al administrador.
* **Flag de Omisión**: Al añadir `--bypass-perms` o `-b`, se omiten las comprobaciones de permiso y se fuerza la apertura del horno sin importar el rango del jugador.
* **Ejemplos**:
  ```bash
  # Forzar a Steve a abrir el horno #1 (valida permisos de Steve)
  /pfadmin force-open Steve 1

  # Forzar a Alex a abrir el horno #4 ignorando comprobación de permisos
  /pfadmin force-open Alex 4 --bypass-perms
  /pfadmin force-open Alex 4 -b
  ```

#### 3. `/pfadmin import <plugin> [--overwrite|-f]`
* **Descripción**: Escanea e importa automáticamente recetas de fundición de plugins externos compatibles (como **Craftorithm**) a archivos `.yml` dentro de `plugins/PlayerFurnaces/recipes/<plugin>/` y recarga el plugin en caliente.
* **Flag de Sobreescritura**: Utiliza `--overwrite` o `-f` para reemplazar archivos de recetas previamente importados.
* **Ejemplos**:
  ```bash
  # Importar todas las recetas de Craftorithm
  /pfadmin import craftorithm

  # Forzar sobreescritura de recetas existentes
  /pfadmin import craftorithm --overwrite
  /pfadmin import craftorithm -f
  ```

#### 4. `/pfadmin reload`
* **Descripción**: Realiza una recarga en caliente de `config.yml`, `messages.yml`, `menus.yml`, todas las recetas (`recipes/` y subcarpetas) y combustibles (`fuels/`).
* **Ejemplo**:
  ```bash
  /pfadmin reload
  ```

---

## 🔒 Estructura de Permisos

PlayerFurnaces dispone de un árbol de permisos modular y jerárquico:

| Nodo de Permiso | Valor por Defecto | Descripción |
| :--- | :--- | :--- |
| `playerfurnaces.command.use` | `true` (Todos) | Permite ejecutar el comando base `/furnace`. |
| `playerfurnaces.admin` | `op` (Operadores) | Acceso total a `/playerfurnacesadmin` y todos sus subcomandos. |
| `playerfurnaces.furnace.1` | `op` | Otorga acceso al Horno Virtual #1. |
| `playerfurnaces.furnace.2` | `op` | Otorga acceso al Horno Virtual #2. |
| `playerfurnaces.furnace.<3-54>` | `false` | Desbloquea el horno individual `<id>`. Ideal para rangos VIP, donadores o recompensas. |

---

## 💡 Ejemplos de Configuración en LuckPerms

### Acceso básico para usuarios iniciales (Hornos 1 y 2)
```bash
/lp group default permission set playerfurnaces.command.use true
/lp group default permission set playerfurnaces.furnace.1 true
/lp group default permission set playerfurnaces.furnace.2 true
```

### Rango VIP (Hornos 1 al 5)
```bash
/lp group vip permission set playerfurnaces.furnace.3 true
/lp group vip permission set playerfurnaces.furnace.4 true
/lp group vip permission set playerfurnaces.furnace.5 true
```

### Acceso administrativo para moderadores / staff
```bash
/lp group admin permission set playerfurnaces.admin true
```
