---
title: Integración y Proveedores de Ítems
description: Manual de arquitectura para ItemResolverRegistry, integraciones nativas con Craftorithm y ExecutableItems, y la API ItemProvider para desarrolladores.
sidebar:
  order: 7
---

# Integración y Proveedores de Ítems Externos

PlayerFurnaces dispone de una arquitectura modular de resolución de ítems gestionada mediante **`ItemResolverRegistry`**. Esto permite a los propietarios de servidores definir recetas y combustibles con ítems creados por plugins de terceros (como **Craftorithm**, **ExecutableItems**, **Oraxen** e **ItemsAdder**), conservando metadatos NBT, etiquetas PDC y texturas durante todo el proceso de fundición.

---

## 🔍 Resolución de Ítems con Namespace (`namespace:item_id`)

Cuando un ítem se declara en una receta o combustible con el formato:

```text
namespace:item_id
```

*(Por ejemplo: `craftorithm:ruby_gem` o `executableitems:excalibur`)*

PlayerFurnaces delega la resolución y verificación al `ItemProvider` registrado para ese espacio de nombres:

```text
+-------------------------------------------------------------+
|                CANAL DE RESOLUCIÓN DE ÍTEMS                 |
+-------------------------------------------------------------+
|                                                             |
|                    ItemResolverRegistry                     |
|                              │                              |
|        ┌─────────────────────┼─────────────────────┐        |
|        ▼                     ▼                     ▼        |
|  [Vanilla Provider]  [Craftorithm Provider] [ExecutableItems] |
|   namespace: vanilla   namespace: craftorithm  namespaces:  |
|                         y crafthorim            executableitems,
|                                                 ei,         |
|                                                 executableitem
+-------------------------------------------------------------+
```

---

## 🛠️ Integraciones Nativas con Plugins

### 1. Integración con Craftorithm
* **Namespaces**: `craftorithm`, `crafthorim`
* **Características**:
  * Se registra automáticamente al iniciar el servidor si se detecta el plugin Craftorithm.
  * Los ingredientes y productos devuelven instancias reales de `ItemStack` conservando texturas personalizadas, CustomModelData y etiquetas PDC de Craftorithm.
  * Compatible de forma nativa con el comando de importación `/pfadmin import craftorithm`.

### 2. Integración con ExecutableItems
* **Namespaces**: `executableitems`, `ei`, `executableitem`
* **Características**:
  * Se registra automáticamente al iniciar si ExecutableItems y SCore están presentes.
  * Reflexión multistrategia para compatibilidad con las diferentes firmas de métodos de `buildItem` a través de distintas versiones de la API.
  * Habilidades, tiempos de reutilización (cooldowns) y variables internas del ítem se conservan intactos en el almacenamiento del horno.
  * Apilado inteligente en salida: PlayerFurnaces normaliza metadatos dinámicos/UUIDs generados en tiempo de ejecución para que los ítems idénticos de ExecutableItems se apilen correctamente en la ranura de salida hasta su límite máximo.
  * Decoración de recetas: Los creadores de recetas pueden añadir nombres con MiniMessage/HEX, descripciones adicionales o etiquetas PDC sobre el ítem final.

---

## 💻 Guía para Desarrolladores: Crear un `ItemProvider` Personalizado

Cualquier desarrollador puede integrar su propio plugin de ítems implementando la interfaz `ItemProvider`.

### 1. Implementar la Interfaz `ItemProvider`

```java
package dev.darkblade.playerfurnaces.provider;

import org.bukkit.inventory.ItemStack;

public interface ItemProvider {

    /**
     * Devuelve el namespace principal gestionado por este proveedor.
     * Ejemplo: "miplugin" para ítems referenciados como "miplugin:item_id".
     */
    String getNamespace();

    /**
     * Construye y devuelve un ItemStack correspondiente al ID y cantidad indicados.
     */
    ItemStack getItem(String id, int amount);

    /**
     * Evalúa si un ItemStack del juego coincide con el ID del proveedor.
     */
    boolean isSimilar(ItemStack itemStack, String id);
}
```

### 2. Ejemplo de Implementación

```java
import dev.darkblade.playerfurnaces.provider.ItemProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EspadasPersonalizadasProvider implements ItemProvider {

    @Override
    public String getNamespace() {
        return "misespadas";
    }

    @Override
    public ItemStack getItem(String id, int amount) {
        if ("espada_fuego".equalsIgnoreCase(id)) {
            ItemStack espada = new ItemStack(Material.DIAMOND_SWORD, amount);
            // Configurar meta, PDC, encantamientos...
            return espada;
        }
        return null;
    }

    @Override
    public boolean isSimilar(ItemStack itemStack, String id) {
        if (itemStack == null) return false;
        // Comprobar etiquetas PDC o lore que identifiquen espada_fuego
        return "espada_fuego".equalsIgnoreCase(id);
    }
}
```

### 3. Registro del Proveedor

Registra tu proveedor durante el `onEnable()` de tu plugin:

```java
ItemResolverRegistry registry = PlayerFurnacesPlugin.getInstance().getItemResolverRegistry();
registry.registerProvider(new EspadasPersonalizadasProvider());
```
