---
title: Introducción a PlayerFurnaces
description: Visión general de PlayerFurnaces, arquitectura, conceptos de hornos virtuales, cálculos delta-time y características clave.
sidebar:
  order: 1
---

# Introducción a PlayerFurnaces

Bienvenido a la documentación oficial de **PlayerFurnaces**, un motor moderno y de alto rendimiento de hornos virtuales diseñado para servidores Paper y Purpur de Minecraft.

PlayerFurnaces proporciona a los jugadores hornos virtuales personales y seguros, accesibles mediante interfaces GUI configurables. Elimina la necesidad de construir extensas granjas y salas de hornos físicos en el mundo, optimizando el rendimiento del servidor y enriqueciendo la experiencia de juego.

---

## ⚡ Características Principales

```text
+-----------------------------------------------------------------------------+
|                        ARQUITECTURA DE PLAYERFURNACES                       |
+-----------------------------------------------------------------------------+
|                                                                             |
|  [CAPA GUI]              /furnace y /pfadmin                                |
|                          Layouts dinámicos (menus.yml)                      |
|                          Estados de ranura interactivos y formato HEX       |
|                                     │                                       |
|                                     ▼                                       |
|  [MOTOR DE FUNDICIÓN]    Cálculo delta-time fuera de línea                  |
|                          Procesamiento asíncrono en segundo plano           |
|                          Seguridad contra robos y shift-click atómico       |
|                                     │                                       |
|                                     ▼                                       |
|  [RECETAS Y COMBUSTIBLE] Definiciones modulares YAML (recipes/ y fuels/)    |
|                          Escaneo recursivo de subdirectorios                |
|                          ItemResolverRegistry (Craftorithm, ExecutableItems)|
|                                     │                                       |
|                                     ▼                                       |
|  [CAPA DE ALMACENAMIENTO]Pool de conexiones HikariCP (SQLite / H2)          |
|                          Serialización binaria NBT (byte[] BLOB)            |
|                          Cero duplicaciones y preservación total de PDC     |
|                                                                             |
+-----------------------------------------------------------------------------+
```

### 1. Cálculos de Fundición Fuera de Línea (Delta-Time)
A diferencia de los hornos tradicionales de Minecraft que requieren cargar chunks y consumir ciclos continuos de ticks, PlayerFurnaces utiliza un eficiente **algoritmo delta-time**:
* Cuando un jugador se desconecta, cierra la interfaz o se descarga el horno, el tickeo activo se detiene.
* Al reabrir el horno o reconectarse, el motor calcula la diferencia exacta de tiempo transcurrido en milisegundos (`System.currentTimeMillis() - lastSmeltTime`).
* Simula instantáneamente la cantidad exacta de ítems cocinados, experiencia acumulada y combustible consumido durante su ausencia.
* **Resultado**: Cero sobrecarga de ticks en chunks inactivos y una progresión de fundición totalmente realista.

### 2. Preservación Total de Ítems Personalizados y Etiquetas PDC
Todos los ítems colocados en hornos virtuales se serializan directamente en arreglos binarios comprimidos (`byte[] BLOB`) mediante la serialización nativa NBT de Bukkit:
* Conserva `CustomModelData`, nombres personalizados (MiniMessage, Hex, códigos legacy), descripciones (lore), encantamientos y valores de daño.
* Preserva etiquetas de `PersistentDataContainer` (PDC) creadas por plugins de ítems como **Craftorithm**, **ExecutableItems**, **Oraxen**, **ItemsAdder** o **MMOItems**.
* Garantiza protección absoluta contra duplicaciones, desincronizaciones de cursor e inyecciones indebidas de inventario.

### 3. Recetas Modulares, Combustibles Personalizados y Resolución Externa
* **Recetas Personalizadas**: Crea recetas de fundición en `recipes/` (con carpetas anidadas recursivas) especificando ingredientes, resultados, duración en ticks, experiencia y modelos personalizados.
* **Sobreescritura Vanilla**: Modifica las recetas de fundición por defecto de Minecraft o desactiva materiales vanilla específicos.
* **Combustibles Personalizados**: Configura combustibles únicos en `fuels/` con duraciones de quemado a medida (`burn-time-ticks`).
* **Proveedores de Ítems**: Integración nativa con namespaces de plugins externos (`craftorithm:item_id`, `executableitems:item_id`, `ei:item_id`).

### 4. Layouts Dinámicos de Menús
* Interfaces totalmente personalizables configuradas en `menus.yml`.
* Diseña cuadrículas visuales únicas para el Hub y los visores de horno mediante cuadrículas ASCII, ítems de relleno decorativos, estados dinámicos de ranura (`smelting`, `idle`, `no_fuel`, `locked`) y texturas de cabezas de jugador.

---

## 🗺️ Guía de la Documentación

Explora las distintas secciones para configurar y sacar el máximo provecho al plugin:

1. [**Configuración General**](configuration/) - Opciones detalladas de `config.yml` y `messages.yml`.
2. [**Menús y Layouts GUI**](gui-menus/) - Personalización de cuadrículas, estados de ranura e ítems en `menus.yml`.
3. [**Comandos y Permisos**](commands-and-permissions/) - Comandos de usuario, inspección administrativa, importación de recetas y jerarquía de permisos.
4. [**Recetas Personalizadas**](custom-recipes/) - Guía de sintaxis para recetas de fundición, sobreescrituras y carpetas recursivas.
5. [**Combustibles Personalizados**](custom-fuels/) - Creación de combustibles y orden de prioridad en la duración de quemado.
6. [**Integración y Proveedores de Ítems**](external-item-providers/) - Integración con Craftorithm, ExecutableItems y API para desarrolladores.
7. [**Base de Datos y Persistencia**](database-and-storage/) - Configuración de SQLite/H2, HikariCP y funcionamiento de la persistencia binaria.
