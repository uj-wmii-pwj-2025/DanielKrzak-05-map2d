package uj.wmii.pwj.map2d;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HashMap2D<R, C, V> implements Map2D<R, C, V> {

    private final Map<R, Map<C, V>> rowStorage;
    private final Map<C, Map<R, V>> columnStorage;
    private int size;

    public HashMap2D() {
        this.rowStorage = new HashMap<>();
        this.columnStorage = new HashMap<>();
        this.size = 0;
    }

    @Override
    public V put(R rowKey, C columnKey, V value) throws NullPointerException {
        if (rowKey == null || columnKey == null) throw new NullPointerException();
        Map<C, V> row = rowStorage.computeIfAbsent(rowKey, k -> new HashMap<>());
        Map<R, V> column = columnStorage.computeIfAbsent(columnKey, k -> new HashMap<>());
        if(!row.containsKey(columnKey)) size++;
        V oldValue = row.put(columnKey, value);
        column.put(rowKey, value);
        return oldValue;
    }

    @Override
    public V get(R rowKey, C columnKey) {
        Map<C, V> view = rowStorage.get(rowKey);
        if (view == null) return null;
        return view.get(columnKey);
    }

    @Override
    public V getOrDefault(R rowKey, C columnKey, V defaultValue) {
        Map<C, V> view = rowStorage.get(rowKey);
        if (view == null) return defaultValue;
        return view.getOrDefault(columnKey, defaultValue);
    }

    @Override
    public V remove(R rowKey, C columnKey) {
        Map<C, V> row = rowStorage.get(rowKey);
        if (row == null) return null;
        Map<R, V> column = columnStorage.get(columnKey);
        if (column == null) return null;
        if(row.containsKey(columnKey)) size--;
        V oldValue = row.remove(columnKey);
        column.remove(rowKey);
        if(row.isEmpty()) rowStorage.remove(rowKey);
        if(column.isEmpty()) columnStorage.remove(columnKey);
        return oldValue;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean nonEmpty() {
        return size > 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        size = 0;
        rowStorage.clear();
        columnStorage.clear();
    }

   @Override
    public Map<C, V> rowView(R rowKey) {
        Map<C, V> view = rowStorage.get(rowKey);
        if (view == null) return Collections.emptyMap();
        return Map.copyOf(view);
    }

    @Override
    public Map<R, V> columnView(C columnKey) {
        Map<R, V> view = columnStorage.get(columnKey);
        if (view == null) return Collections.emptyMap();
        return Map.copyOf(view);
    }

    @Override
    public boolean containsValue(V value) {
        for(Map<C, V> colMap : rowStorage.values()){
            if(colMap.containsValue(value)) return true;
        }
        return false;
    }

    @Override
    public boolean containsKey(R rowKey, C columnKey) {
        Map<C, V> row = rowStorage.get(rowKey);
        if(row == null) return false;
        return row.containsKey(columnKey);
    }

    @Override
    public boolean containsRow(R rowKey) {
        return rowStorage.containsKey(rowKey);
    }

    @Override
    public boolean containsColumn(C columnKey) {
        return columnStorage.containsKey(columnKey);
    }

    @Override
    public Map<R, Map<C, V>> rowMapView() {
        if(isEmpty()) return Collections.emptyMap();
        Map<R, Map<C, V>> deepCopy = new HashMap<>();
        for (Map.Entry<R, Map<C, V>> entry : rowStorage.entrySet()) {
            deepCopy.put(entry.getKey(), Map.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(deepCopy);
    }

    @Override
    public Map<C, Map<R, V>> columnMapView() {
        if(isEmpty()) return Collections.emptyMap();
        Map<C, Map<R, V>> deepCopy = new HashMap<>();
        for (Map.Entry<C, Map<R, V>> entry : columnStorage.entrySet()) {
            deepCopy.put(entry.getKey(), Map.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(deepCopy);
    }

    @Override
    public Map2D<R, C, V> fillMapFromRow(Map<? super C, ? super V> target, R rowKey) {
        Map <C, V> row =  rowStorage.get(rowKey);
        if (row != null) target.putAll(row);
        return this;
    }

    @Override
    public Map2D<R, C, V> fillMapFromColumn(Map<? super R, ? super V> target, C columnKey) {
        Map <R, V> column =  columnStorage.get(columnKey);
        if (column != null) target.putAll(column);
        return this;
    }

   @Override
    public Map2D<R, C, V> putAll(Map2D<? extends R, ? extends C, ? extends V> source) {
        for(var rowEntry : source.rowMapView().entrySet()) {
            for (var columnEntry : rowEntry.getValue().entrySet()) {
                put(rowEntry.getKey(), columnEntry.getKey(), columnEntry.getValue());
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToRow(Map<? extends C, ? extends V> source, R rowKey) {
        for (var entry : source.entrySet()) {
            put(rowKey, entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToColumn(Map<? extends R, ? extends V> source, C columnKey) {
        for (var entry : source.entrySet()) {
            put(entry.getKey(), columnKey, entry.getValue());
        }
        return this;
    }

   @Override
    public <R2, C2, V2> Map2D<R2, C2, V2> copyWithConversion(Function<? super R, ? extends R2> rowFunction, Function<? super C, ? extends C2> columnFunction, Function<? super V, ? extends V2> valueFunction) {
        Map2D<R2, C2, V2> newMap = new HashMap2D<>();
        for(var rowEntry : rowStorage.entrySet()) {
            for (var columnEntry : rowEntry.getValue().entrySet()) {
                newMap.put(rowFunction.apply(rowEntry.getKey()), columnFunction.apply(columnEntry.getKey()), valueFunction.apply(columnEntry.getValue()));
            }
        }
        return newMap;
    }
}
