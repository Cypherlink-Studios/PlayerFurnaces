package dev.darkblade.playerfurnaces.provider.impl;

import dev.darkblade.playerfurnaces.provider.ItemProvider;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Optional;

public class ExecutableItemsItemProvider implements ItemProvider {

    private volatile boolean available = false;
    private Object targetInstance = null;
    private Method getExecutableItemByIdMethod = null;
    private Method getExecutableItemByStackMethod = null;

    public ExecutableItemsItemProvider() {
        ensureInitialized();
    }

    private synchronized void ensureInitialized() {
        if (available && getExecutableItemByIdMethod != null && getExecutableItemByStackMethod != null) {
            return;
        }

        try {
            if (Bukkit.getServer() == null || Bukkit.getPluginManager() == null) {
                return;
            }
            Plugin plugin = Bukkit.getPluginManager().getPlugin("ExecutableItems");
            if (plugin == null || !plugin.isEnabled()) {
                return;
            }
        } catch (Throwable ignored) {
            return;
        }

        // Strategy 1: ExecutableItemsAPI.getExecutableItemsManager()
        try {
            Class<?> apiClass = Class.forName("com.ssomar.score.api.executableitems.ExecutableItemsAPI");
            Method getManagerMethod = apiClass.getMethod("getExecutableItemsManager");
            Object manager = getManagerMethod.invoke(null);
            if (manager != null) {
                if (bindMethods(manager, manager.getClass())) {
                    this.targetInstance = manager;
                    this.available = true;
                    return;
                }
            }
        } catch (Throwable ignored) {
        }

        // Strategy 2: ExecutableItemsAPI static methods
        try {
            Class<?> apiClass = Class.forName("com.ssomar.score.api.executableitems.ExecutableItemsAPI");
            if (bindMethods(null, apiClass)) {
                this.targetInstance = null;
                this.available = true;
                return;
            }
        } catch (Throwable ignored) {
        }

        // Strategy 3: ExecutableItemsManager.getInstance()
        try {
            Class<?> managerClass = Class.forName("com.ssomar.score.executableitems.ExecutableItemsManager");
            Method getInstanceMethod = managerClass.getMethod("getInstance");
            Object manager = getInstanceMethod.invoke(null);
            if (manager != null) {
                if (bindMethods(manager, manager.getClass())) {
                    this.targetInstance = manager;
                    this.available = true;
                    return;
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private boolean bindMethods(Object instance, Class<?> clazz) {
        Method byId = null;
        Method byStack = null;

        for (Method m : clazz.getMethods()) {
            if (m.getName().equals("getExecutableItem")) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length == 1) {
                    if (params[0] == String.class) {
                        byId = m;
                        byId.setAccessible(true);
                    } else if (ItemStack.class.isAssignableFrom(params[0])) {
                        byStack = m;
                        byStack.setAccessible(true);
                    }
                }
            }
        }

        if (byId != null || byStack != null) {
            this.getExecutableItemByIdMethod = byId;
            this.getExecutableItemByStackMethod = byStack;
            return true;
        }
        return false;
    }

    @Override
    public String getNamespace() {
        return "executableitems";
    }

    @Override
    public ItemStack getItem(String id, int amount) {
        ensureInitialized();

        if (!available || getExecutableItemByIdMethod == null || id == null || id.trim().isEmpty()) {
            return null;
        }

        try {
            Object result = getExecutableItemByIdMethod.invoke(targetInstance, id.trim());
            if (result instanceof Optional<?> opt) {
                result = opt.orElse(null);
            }
            if (result != null) {
                for (Method m : result.getClass().getMethods()) {
                    if (!m.getName().equals("buildItem")) {
                        continue;
                    }
                    try {
                        m.setAccessible(true);
                        Class<?>[] params = m.getParameterTypes();
                        Object itemStackObj = null;
                        if (params.length == 3 && (params[0] == int.class || params[0] == Integer.class)) {
                            itemStackObj = m.invoke(result, amount, Optional.empty(), Optional.empty());
                        } else if (params.length == 2 && (params[0] == int.class || params[0] == Integer.class)) {
                            itemStackObj = m.invoke(result, amount, Optional.empty());
                        } else if (params.length == 1 && (params[0] == int.class || params[0] == Integer.class)) {
                            itemStackObj = m.invoke(result, amount);
                        }
                        if (itemStackObj instanceof ItemStack itemStack) {
                            ItemStack copy = itemStack.clone();
                            copy.setAmount(amount);
                            return copy;
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    @Override
    public boolean isSimilar(ItemStack itemStack, String id) {
        if (itemStack == null || itemStack.getType().isAir() || id == null || id.trim().isEmpty()) {
            return false;
        }

        ensureInitialized();
        String targetId = id.trim();

        // 1. Primary strategy: Official ExecutableItems API stack inspection
        if (available && getExecutableItemByStackMethod != null) {
            try {
                Object opt = getExecutableItemByStackMethod.invoke(targetInstance, itemStack);
                if (opt instanceof Optional<?> optional && optional.isPresent()) {
                    Object execItem = optional.get();
                    Method getIdMethod = execItem.getClass().getMethod("getId");
                    getIdMethod.setAccessible(true);
                    Object foundIdObj = getIdMethod.invoke(execItem);
                    if (foundIdObj != null) {
                        return targetId.equalsIgnoreCase(foundIdObj.toString().trim());
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        // 2. Fallback: PersistentDataContainer inspection (if saveInPDC is enabled or bridged)
        if (Bukkit.getServer() != null) {
            try {
                if (itemStack.hasItemMeta()) {
                    var pdc = itemStack.getItemMeta().getPersistentDataContainer();
                    for (var key : pdc.getKeys()) {
                        String ns = key.getNamespace().toLowerCase();
                        if (ns.contains("executableitem") || ns.equalsIgnoreCase("ei") || ns.contains("ssomar")) {
                            String value = pdc.get(key, org.bukkit.persistence.PersistentDataType.STRING);
                            if (targetId.equalsIgnoreCase(value)) {
                                return true;
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        // 3. Fallback: Direct comparison if stack inspection API is not available
        if (getExecutableItemByStackMethod == null && Bukkit.getServer() != null) {
            try {
                ItemStack execItem = getItem(targetId, 1);
                if (execItem != null) {
                    return itemStack.isSimilar(execItem);
                }
            } catch (Throwable ignored) {
            }
        }

        return false;
    }

    public boolean isAvailable() {
        ensureInitialized();
        return available;
    }
}
