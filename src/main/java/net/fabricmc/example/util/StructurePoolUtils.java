package net.fabricmc.example.util;

import net.minecraft.structure.pool.StructurePool;
import net.fabricmc.example.mixin.StructurePoolAccessor;

import java.util.ArrayList;

public class StructurePoolUtils {
    public static void appendPool(StructurePool primaryPool, StructurePool secondaryPool)
    {
        var primaryPoolAccessor = (StructurePoolAccessor) primaryPool;
        var secondaryPoolAccessor = (StructurePoolAccessor) secondaryPool;

        var elementCounts = new ArrayList<>(primaryPoolAccessor.getElementCounts());
        var elements = new ArrayList<>(primaryPoolAccessor.getElements());

        elementCounts.addAll(secondaryPoolAccessor.getElementCounts());
        elements.addAll(secondaryPoolAccessor.getElements());

        primaryPoolAccessor.setElements(elements);
        primaryPoolAccessor.setElementCounts(elementCounts);
    }
}
