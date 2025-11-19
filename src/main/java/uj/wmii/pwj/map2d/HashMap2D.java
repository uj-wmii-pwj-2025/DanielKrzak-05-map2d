package uj.wmii.pwj.map2d;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

    /**
     * Puts a value to the map, at given row and columns keys.
     * If specified row-column key already contains element, it should be replaced.
     *
     * @param rowKey    row part of the key.
     * @param columnKey column part of the key.
     * @param value     object to put. It can be null.
     * @return object, that was previously contained by map within these coordinates, or {@code null} if it was empty.
     * @throws NullPointerException if rowKey or columnKey are {@code null}.
     */
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

    /**
     * Gets a value from the map from given key.
     *
     * @param rowKey    row part of a key.
     * @param columnKey column part of a key.
     * @return object contained at specified key, or {@code null}, if the key does not contain an object.
     */
    @Override
    public V get(R rowKey, C columnKey) {
        Map<C, V> view = rowStorage.get(rowKey);
        if (view == null) return null;
        return view.get(columnKey);
    }

    /**
     * Gets a value from the map from given key. If specified value does not exist, returns {@code defaultValue}.
     *
     * @param rowKey       row part of a key.
     * @param columnKey    column part of a key.
     * @param defaultValue value to be returned, if specified key does not contain a value.
     * @return object contained at specified key, or {@code defaultValue}, if the key does not contain an object.
     */
    @Override
    public V getOrDefault(R rowKey, C columnKey, V defaultValue) {
        Map<C, V> view = rowStorage.get(rowKey);
        if (view == null) return defaultValue;
        return view.getOrDefault(columnKey, defaultValue);
    }

    /**
     * Removes a value from the map from given key.
     *
     * @param rowKey    row part of a key.
     * @param columnKey column part of a key.
     * @return object contained at specified key, or {@code null}, if the key didn't contain an object.
     */
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

    /**
     * Checks if map contains no elements.
     *
     * @return {@code true} if map doesn't contain any values; {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Checks if map contains any element.
     *
     * @return {@code true} if map contains at least one value; {@code false} otherwise.
     */
    @Override
    public boolean nonEmpty() {
        return size > 0;
    }

    /**
     * Return number of values being stored by this map.
     *
     * @return number of values being stored
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Removes all objects from a map.
     */
    @Override
    public void clear() {
        size = 0;
        rowStorage.clear();
        columnStorage.clear();
    }

    /**
     * Returns a view of mappings for specified key.
     * Result map should be immutable. Later changes to this map should not affect result map.
     *
     * @param rowKey row key to get view map for.
     * @return map with view of particular row. If there is no values associated with specified row, empty map is returned.
     */
    @Override
    public Map<C, V> rowView(R rowKey) {
        Map<C, V> view = rowStorage.get(rowKey);
        if (view == null) return Collections.emptyMap();
        return Map.copyOf(view);
    }

    /**
     * Returns a view of mappings for specified column.
     * Result map should be immutable. Later changes to this map should not affect returned map.
     *
     * @param columnKey column key to get view map for.
     * @return map with view of particular column. If there is no values associated with specified column, empty map is returned.
     */
    @Override
    public Map<R, V> columnView(C columnKey) {
        Map<R, V> view = columnStorage.get(columnKey);
        if (view == null) return Collections.emptyMap();
        return Map.copyOf(view);
    }

    /**
     * Checks if map contains specified value.
     *
     * @param value value to be checked
     * @return {@code true} if map contains specified value; {@code false} otherwise.
     */
    @Override
    public boolean containsValue(V value) {
        for(Map<C, V> colMap : rowStorage.values()){
            if(colMap.containsValue(value)) return true;
        }
        return false;
    }

    /**
     * Checks if map contains a value under specified key.
     *
     * @param rowKey    row key to be checked
     * @param columnKey column key to be checked
     * @return {@code true} if map contains specified key; {@code false} otherwise.
     */
    @Override
    public boolean containsKey(R rowKey, C columnKey) {
        Map<C, V> row = rowStorage.get(rowKey);
        if(row == null) return false;
        return row.containsKey(columnKey);
    }

    /**
     * Checks if map contains at least one value within specified row.
     *
     * @param rowKey row to be checked
     * @return {@code true} if map at least one value within specified row; {@code false} otherwise.
     */
    @Override
    public boolean containsRow(R rowKey) {
        return rowStorage.containsKey(rowKey);
    }

    /**
     * Checks if map contains at least one value within specified column.
     *
     * @param columnKey column to be checked
     * @return {@code true} if map at least one value within specified column; {@code false} otherwise.
     */
    @Override
    public boolean containsColumn(C columnKey) {
        return columnStorage.containsKey(columnKey);
    }

    /**
     * Return a view of this map as map of rows to map of columns to values.
     * Result map should be immutable. Later changes to this map should not affect returned map.
     *
     * @return map with row-based view of this map. If this map is empty, empty map should be returned.
     */
    @Override
    public Map<R, Map<C, V>> rowMapView() {
        if(isEmpty()) return Collections.emptyMap();
        Map<R, Map<C, V>> deepCopy = new HashMap<>();
        for (Map.Entry<R, Map<C, V>> entry : rowStorage.entrySet()) {
            deepCopy.put(entry.getKey(), Map.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(deepCopy);
    }

    /**
     * Return a view of this map as map of columns to map of rows to values.
     * Result map should be immutable. Later changes to this map should not affect returned map.
     *
     * @return map with column-based view of this map. If this map is empty, empty map should be returned.
     */
    @Override
    public Map<C, Map<R, V>> columnMapView() {
        if(isEmpty()) return Collections.emptyMap();
        Map<C, Map<R, V>> deepCopy = new HashMap<>();
        for (Map.Entry<C, Map<R, V>> entry : columnStorage.entrySet()) {
            deepCopy.put(entry.getKey(), Map.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(deepCopy);
    }

    /**
     * Fills target map with all key-value maps from specified row.
     *
     * @param target map to be filled
     * @param rowKey row key to get data to fill map from
     * @return this map (floating)
     */
    @Override
    public Map2D<R, C, V> fillMapFromRow(Map<? super C, ? super V> target, R rowKey) {
        Map <C, V> row =  rowStorage.get(rowKey);
        if (row != null) target.putAll(row);
        return this;
    }

    /**
     * Fills target map with all key-value maps from specified row.
     *
     * @param target    map to be filled
     * @param columnKey column key to get data to fill map from
     * @return this map (floating)
     */
    @Override
    public Map2D<R, C, V> fillMapFromColumn(Map<? super R, ? super V> target, C columnKey) {
        Map <R, V> column =  columnStorage.get(columnKey);
        if (column != null) target.putAll(column);
        return this;
    }

    /**
     * Puts all content of {@code source} map to this map.
     *
     * @param source map to make a copy from
     * @return this map (floating)
     */
    @Override
    public Map2D<R, C, V> putAll(Map2D<? extends R, ? extends C, ? extends V> source) {
        for(var rowEntry : source.rowMapView().entrySet()) {
            for (var columnEntry : rowEntry.getValue().entrySet()) {
                put(rowEntry.getKey(), columnEntry.getKey(), columnEntry.getValue());
            }
        }
        return this;
    }

    /**
     * Puts all content of {@code source} map to this map under specified row.
     * Ech key of {@code source} map becomes a column part of key.
     *
     * @param source map to make a copy from
     * @param rowKey object to use as row key
     * @return this map (floating)
     */
    @Override
    public Map2D<R, C, V> putAllToRow(Map<? extends C, ? extends V> source, R rowKey) {
        for (var entry : source.entrySet()) {
            put(rowKey, entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Puts all content of {@code source} map to this map under specified column.
     * Ech key of {@code source} map becomes a row part of key.
     *
     * @param source    map to make a copy from
     * @param columnKey object to use as column key
     * @return this map (floating)
     */
    @Override
    public Map2D<R, C, V> putAllToColumn(Map<? extends R, ? extends V> source, C columnKey) {
        for (var entry : source.entrySet()) {
            put(entry.getKey(), columnKey, entry.getValue());
        }
        return this;
    }

    /**
     * Creates a copy of this map, with application of conversions for rows, columns and values to specified types.
     * If as result of row or column conversion result key duplicates, then appropriate row and / or column in target map has to be merged.
     * If merge ends up in key duplication, then it's up to specific implementation which value from possible to choose.
     *
     * @param rowFunction    function converting row part of key
     * @param columnFunction function converting column part of key
     * @param valueFunction  function converting value
     * @return new instance of {@code Map2D} with converted objects
     */
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
