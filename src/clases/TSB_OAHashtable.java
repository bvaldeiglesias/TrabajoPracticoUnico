package clases;



import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author TSB_Team
 * @param <K> el tipo de los objetos que serán usados como clave en la tabla.
 * @param <V> el tipo de los objetos que serán los valores de la tabla.
 */
public class TSB_OAHashtable<K, V> implements Map<K, V>, Serializable {

    // el tamaño máximo que podrá tener el arreglo de soprte...
    private final static int MAX_SIZE = Integer.MAX_VALUE;
    // la tabla hash: el arreglo que contiene las listas de desborde...
    private Map.Entry<K, V>[] table;
    // el tamaño inicial de la tabla (tamaño con el que fue creada)...
    private int initial_capacity;
    // la cantidad de objetos que contiene la tabla en TODAS sus listas...
    private int count;
    // el factor de carga para calcular si hace falta un rehashing...
    private float load_factor;
    // conteo de operaciones de cambio de tamaño (fail-fast iterator).
    protected transient int modCount;

    /*
     * (Tal cual están definidos en la clase java.util.Hashtable)
     * Cada uno de estos campos se inicializa para contener una instancia de la
     * vista que sea más apropiada, la primera vez que esa vista es requerida. 
     * La vista son objetos stateless (no se requiere que almacenen datos, sino 
     * que sólo soportan operaciones), y por lo tanto no es necesario crear más 
     * de una de cada una.
     */
    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K, V>> entrySet = null;
    private transient Collection<V> values = null;

    /**
     * Crea una tabla vacía, con la capacidad inicial igual a 11 y con factor de
     * carga igual a 0.5f.
     */
    public TSB_OAHashtable() {
        this(5, 0.5f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con factor de
     * carga igual a 0.5f.
     *
     * @param initial_capacity la capacidad inicial de la tabla.
     */
    public TSB_OAHashtable(int initial_capacity) {
        this(initial_capacity, 0.5f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con el factor
     * de carga indicado. Si la capacidad inicial indicada por initial_capacity
     * es menor o igual a 0, la tabla será creada de tamaño 11. Si el factor de
     * carga indicado es negativo o cero, se ajustará a 0.5f.
     *
     * @param initial_capacity la capacidad inicial de la tabla.
     * @param load_factor el factor de carga de la tabla.
     */
    public TSB_OAHashtable(int initial_capacity, float load_factor) {
        this.initial_capacity = initial_capacity;
        this.load_factor = load_factor;

        if (load_factor <= 0) {
            load_factor = 0.5f;
        }
        if (initial_capacity <= 0) {
            initial_capacity = 11;
        } else if (initial_capacity > TSB_OAHashtable.MAX_SIZE) {
            initial_capacity = TSB_OAHashtable.MAX_SIZE;
        }

        this.table = new Map.Entry[initial_capacity];
        this.count = 0;
        this.modCount = 0;
    }

    /**
     * Crea una tabla a partir del contenido del Map especificado.
     *
     * @param t el Map a partir del cual se creará la tabla.
     */
    public TSB_OAHashtable(Map<? extends K, ? extends V> t) {
        this(11, 0.5f);
        this.putAll(t);
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public boolean isEmpty() {
        return (this.count == 0);
    }

    @Override
    public boolean containsKey(Object key) {
        return (this.get((K) key) != null);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.contains(value);
    }

    /**
     * Retorna el objeto al cual está asociada la clave key en la tabla, o null
     * si la tabla no contiene ningún objeto asociado a esa clave.
     *
     * @param key la clave que será buscada en la tabla.
     * @return el objeto asociado a la clave especificada (si existe la clave) o
     * null (si no existe la clave en esta tabla).
     * @throws NullPointerException si key es null.
     * @throws ClassCastException si la clase de key no es compatible con la
     * tabla.
     */
    @Override
    public V get(Object key) {
        isNull(key);

        int ib = this.h(key.hashCode());

        Map.Entry<K, V> x = this.search_for_entry((K) key);
        return (x != null) ? x.getValue() : null;
    }

    public void isNull(Object x) {
        if (x == null) {
            throw new NullPointerException("El parametro ingresado no debe ser null");
        }
    }

    /**
     * Asocia el valor (value) especificado, con la clave (key) especificada en
     * esta tabla. Si la tabla contenía previamente un valor asociado para la
     * clave, entonces el valor anterior es reemplazado por el nuevo (y en este
     * caso el tamaño de la tabla no cambia).
     *
     * @param key la clave del objeto que se quiere agregar a la tabla.
     * @param value el objeto que se quiere agregar a la tabla.
     * @return el objeto anteriormente asociado a la clave si la clave ya estaba
     * asociada con alguno, o null si la clave no estaba antes asociada a ningún
     * objeto.
     * @throws NullPointerException si key es null o value es null.
     */
    @Override
    public V put(K key, V value) {
        isNull(key);
        isNull(value);

        int ib = this.h(key);

        V old = null;

        if (this.count >= this.load_factor * table.length) {
            this.rehash();
        }

        if (this.containsKey(key)) {
           SearchIterator it = new SearchIterator (h(key.hashCode()));
            while (it.hasNext()) {
                Map.Entry<K, V> entry = it.next();
                if (entry.getKey().equals(key)) {
                    it.setValue(value);
                    break;
                }
            }

        } else {
            AddIterator it = new AddIterator(h(key.hashCode()));
            while (it.hasNext()) {
                Map.Entry<K, V> entry = it.next();
                if (entry == null || entry.getClass().equals(Tombstone.class)) {
                    this.table[it.current] = new Entry(key, value) ;
                    break;
                }
            }
            this.count++;
        }

        
        this.modCount++;

        return old;
    }
    

    /**
     * Elimina de la tabla la clave key (y su correspondiente valor asociado).
     * El método no hace nada si la clave no está en la tabla.
     *
     * @param key la clave a eliminar.
     * @return El objeto al cual la clave estaba asociada, o null si la clave no
     * estaba en la tabla.
     * @throws NullPointerException - if the key is null.
     */
    @Override
    public V remove(Object key) {
        isNull(key);

        int ib = this.h(key.hashCode());
        V old = null;

        if (this.containsKey( (K) key)) {
            SearchIterator it = new SearchIterator(h(key.hashCode()));
            while (it.hasNext()) {
                Map.Entry<K, V> entry = it.next();
                if (entry.getKey().equals(key)) {
                    it.remove();
                    break;
                }
            }
            this.count--;
            this.modCount++;

        }
        
        return old;
    }

    /**
     * Copia en esta tabla, todos los objetos contenidos en el map especificado.
     * Los nuevos objetos reemplazarán a los que ya existan en la tabla
     * asociados a las mismas claves (si se repitiese alguna).
     *
     * @param m el map cuyos objetos serán copiados en esta tabla.
     * @throws NullPointerException si m es null.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Elimina todo el contenido de la tabla, de forma de dejarla vacía. En esta
     * implementación además, el arreglo de soporte vuelve a tener el tamaño que
     * inicialmente tuvo al ser creado el objeto.
     */
    @Override
    public void clear() {
        this.table = new Map.Entry[initial_capacity];
        this.count = 0;
        this.modCount++;
    }

    /**
     * Retorna un Set (conjunto) a modo de vista de todas las claves (key)
     * contenidas en la tabla. El conjunto está respaldado por la tabla, por lo
     * que los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa. Si la tabla es modificada mientras un iterador está actuando
     * sobre el conjunto vista, el resultado de la iteración será indefinido
     *
     * @return un conjunto (un Set) a modo de vista de todas las claves mapeadas
     * en la tabla.
     */
    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new KeySet();
        }
        return keySet;
    }

    /**
     * Retorna una Collection (colección) a modo de vista de todos los valores
     * (values) contenidos en la tabla. La colección está respaldada por la
     * tabla, por lo que los cambios realizados en la tabla serán reflejados en
     * la colección, y viceversa. Si la tabla es modificada mientras un iterador
     * está actuando sobre la colección vista, el resultado de la iteración será
     * indefinido
     *
     * @return una colección (un Collection) a modo de vista de todas los
     * valores mapeados en la tabla.
     */
    @Override
    public Collection<V> values() {
        if (values == null) {
            // values = Collections.synchronizedCollection(new ValueCollection());
            values = new ValueCollection();
        }
        return values;
    }

    /**
     * Retorna un Set (conjunto) a modo de vista de todos los pares (key, value)
     * contenidos en la tabla. El conjunto está respaldado por la tabla, por lo
     * que los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa. Si la tabla es modificada mientras un iterador está actuando
     * sobre el conjunto vista, el resultado de la iteración será indefinido
     *
     * @return un conjunto (un Set) a modo de vista de todos los objetos
     * mapeados en la tabla.
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();          
        }
        return entrySet;
    }

    //************************ Redefinición de métodos heredados desde Object.
    /**
     * Retorna una copia superficial de la tabla. Las listas de desborde o
     * buckets que conforman la tabla se clonan ellas mismas, pero no se clonan
     * los objetos que esas listas contienen: en cada bucket de la tabla se
     * almacenan las direcciones de los mismos objetos que contiene la original.
     *
     * @return una copia superficial de la tabla.
     * @throws java.lang.CloneNotSupportedException si la clase no implementa la
     * interface Cloneable.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        TSB_OAHashtable<K, V> t = (TSB_OAHashtable<K, V>) super.clone();
        t.table = new Map.Entry[table.length];
        for (int i = table.length; i-- > 0;) {
            t.table[i] = (Map.Entry<K, V>) table[i];
        }
        t.keySet = null;
        t.entrySet = null;
        t.values = null;
        t.modCount = 0;
        return t;
    }

    /**
     * Determina si esta tabla es igual al objeto espeficicado.
     *
     * @param obj el objeto a comparar con esta tabla.
     * @return true si los objetos son iguales.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Map)) {
            return false;
        }

        Map<K, V> t = (Map<K, V>) obj;
        if (t.size() != this.size()) {
            return false;
        }

        try {
            Iterator<Map.Entry<K, V>> i = this.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<K, V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();

                if (!value.equals(t.get(key))) {
                    return false;
                }

            }
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * Retorna un hash code para la tabla completa.
     *
     * @return un hash code para la tabla.
     */
    @Override
    public int hashCode() {
        if (this.isEmpty()) {
            return 0;
        }

        int hc = 0;
        for (Map.Entry<K, V> entry : this.entrySet()) {
            hc += entry.hashCode();
        }
        return hc;
    }

    /**
     * Devuelve el contenido de la tabla en forma de String.
     *
     * @return una cadena con el contenido completo de la tabla.
     */
    @Override
    public String toString() {
        StringBuilder cad = new StringBuilder("Table: { \n");
        for (int i = 0; i < this.table.length; i++) {
            if (this.table[i] != null && !(this.table[i].getClass().equals(Tombstone.class))) {
              cad.append(this.table[i].toString());
            }   
        }
        cad.append("}");
        return cad.toString();
    }

    //************************ Métodos específicos de la clase.
    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que
     * entra como parámetro. Equivale a containsValue().
     *
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */
    public boolean contains(Object value) {
        if (value == null) {
            return false;
        }

        for (Map.Entry<K, V> entry : this.table) {
            if (value.equals(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Incrementa el tamaño de la tabla y reorganiza su contenido. Se invoca
     * automaticamente cuando se detecta que la cantidad promedio de nodos por
     * lista supera a cierto valor critico dado por (10 * load_factor).
     */
    protected void rehash() {
        int old_length = this.table.length;

        // nuevo tamaño: doble del anterior, más uno para llevarlo a impar...
        int new_length = old_length * 2 + 1;

        // no permitir que la tabla tenga un tamaño mayor al límite máximo...
        // ... para evitar overflow y/o desborde de índices...
        if (new_length > TSB_OAHashtable.MAX_SIZE) {
            new_length = TSB_OAHashtable.MAX_SIZE;
        }

        Map.Entry<K, V> old[] = this.table;

        this.table = new Map.Entry[new_length];

        // notificación fail-fast iterator... la tabla cambió su estructura...
        this.modCount++;

        // recorrer el viejo arreglo y redistribuir los objetos que tenia...
        for (Map.Entry<K, V> entry : old) {
            if (entry != null && !(entry.getClass().equals(Tombstone.class)) ) {
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }

    //************************ Métodos privados.
    /*
     * Función hash. Toma una clave entera k y calcula y retorna un índice 
     * válido para esa clave para entrar en la tabla.     
     */
    private int h (int k) {
        return h(k, this.table.length);
    }

    /*
     * Función hash. Toma una clave entera k y un tamaño de tabla t, y calcula y 
     * retorna un índice válido para esa clave dado ese tamaño. 
     * Multiplicamos aleatoriamente a k y t por primos para minimizar el numero de colisiones.
     */
    private int h(int k, int t) {
        int auxK, auxT;
        if (k < 0) {
            k *= -1;
        }
        if (k % 2 == 0) {
            auxK = k * 103;
        } else {
            auxK = k * 113;
        }
        if (t % 2 == 0) {
            auxT = t * 37;
        } else {
            auxT = t * 47;
        }
        return k % t;
    }

    /*
     * Función hash. Toma un objeto key que representa una clave y calcula y 
     * retorna un índice válido para esa clave para entrar en la tabla.     
     */
    private int h(K key) {
        return h(key.hashCode(), this.table.length);
    }

    /*
     * Función hash. Toma un objeto key que representa una clave y un tamaño de 
     * tabla t, y calcula y retorna un índice válido para esa clave dedo ese
     * tamaño.     
     */
    private int h(K key, int t) {
        return h(key.hashCode(), t);
    }

    /*
     * Busca en la tabla un objeto Entry cuya clave coincida con key.
     * Si lo encuentra, retorna ese objeto Entry. Si no lo encuentra, retorna 
     * null.
     */
    private Map.Entry<K, V> search_for_entry(K key) {
        if (this.count == 0) {
            return null;
        }
        int k = h(key.hashCode());
        SearchIterator it = new SearchIterator(k);
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            if (entry != null && key.equals(entry.getKey())) {
                return entry;
            }
        }
        return null;
    }
    
    
    
    

    //************************ Clases Internas.
    /**
     * Clase interna de un iterador para recorrer la lista de manera
     * exponencial, y no lineal.
     */
    private class SearchIterator implements Iterator<Map.Entry<K,V>> {

        private int current; //indice del elemento que hay que procesar
        private boolean next_ok; // true: next fue invocado (usado por remove()...)
        private int valorCuadratico = 0; //indice utilizado para el avance cuadratico
        
        public SearchIterator(int index) {
            current = index;
            next_ok = false;
        }

        @Override
        public boolean hasNext() {
            if (table == null) {
                return false;
            }

            int aux = current + (valorCuadratico * valorCuadratico);
            if (aux >= table.length) {
                aux = (current % table.length);
            }
            if (table[aux] == null) {
                return false;
            }

            return true;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No quedan elementos por recorrer");
            }
            
            
            
            current += (valorCuadratico*valorCuadratico);
            valorCuadratico++;
            if (current >= table.length) {
                    current = (current % table.length);     
            }
            Map.Entry next = table[current];
            next_ok = true;    

            return next;
        }
        
        @Override
        public void remove() {
            if (!next_ok) {
                throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
            }
            count--;
            table[current] = new Tombstone();
            
            next_ok = false;
        }
        
        public void setValue(V value){
            table[current].setValue(value);
        }
    }
    
    private class AddIterator implements Iterator<Map.Entry<K,V>> {

        private int current; //indice del elemento que hay que procesar
        private int valorCuadratico = 0; //indice utilizado para el avance cuadratico
        
        public AddIterator(int index) {
            current = index;
        }

        @Override
        public boolean hasNext() {
            if (table == null) return false;
            if (current > table.length) {
                return false;
            }

            return true;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No quedan elementos por recorrer");
            }

            current += (valorCuadratico*valorCuadratico);
            valorCuadratico++;
            if (current >= table.length) {
                current = (current % table.length);
            }
            Map.Entry next = table[current];

            return next;
        }

    }

    /*
     * Clase interna que representa los pares de objetos que se almacenan en la
     * tabla hash: son instancias de esta clase las que realmente se guardan en 
     * cada una de las listas del arreglo table que se usa como soporte de 
     * la tabla. Lanzará una IllegalArgumentException si alguno de los dos 
     * parámetros es null.
     */
    private class Entry<K, V> implements Map.Entry<K, V>, Serializable {

        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new IllegalArgumentException("setValue(): parámetro null...");
            }
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + Objects.hashCode(this.key);
            hash = 53 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Entry<?, ?> other = (Entry<?, ?>) obj;
            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Entry{" + "key=" + key + ", value=" + value + '}' + "\n" ;
        }
    }

    /*
     * Clase interna que representa una vista de todas los Claves mapeadas en la
     * tabla: si la vista cambia, cambia también la tabla que le da respaldo, y
     * viceversa. La vista es stateless: no mantiene estado alguno (es decir, no 
     * contiene datos ella misma, sino que accede y gestiona directamente datos
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la  
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */
    private class KeySet extends AbstractSet<K> {

        @Override
        public Iterator<K> iterator() {
            return new KeySetIterator();
        }

        @Override
        public int size() {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public void clear() {
            TSB_OAHashtable.this.clear();
        }

        @Override
        public boolean remove(Object o) {
            return (TSB_OAHashtable.this.remove(o) != null);
        }

        @Override
        public boolean contains(Object o) {
            return TSB_OAHashtable.this.containsKey(o);
        }

        private class KeySetIterator implements Iterator<K> {
            private int current = 0; //indice del elemento que hay que procesar
            private boolean next_ok; // true: next fue invocado (usado por remove()...)

            public KeySetIterator() {
                
            }

            @Override
            public boolean hasNext() {
                if (TSB_OAHashtable.this.isEmpty()) {
                    return false;
                }
                if (current + 1 >= table.length) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public K next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No quedan elementos por recorrer");

                }

                current++;
                next_ok = true;

                return table[current].getKey();
            }

            @Override
            public void remove() {
                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }
                count--;
                table[current] = new Tombstone();

                next_ok = false;
            }
        }
    }
    
    private class EntrySet extends AbstractSet<Map.Entry<K,V>>{

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntrySetIterator();
        }
        
        /*
         * Verifica si esta vista (y por lo tanto la tabla) contiene al par 
         * que entra como parámetro (que debe ser de la clase Entry).
         */
        @Override
        public boolean contains(Object o) {
            if(o == null) { return false; } 
            if(!(o instanceof Entry)) { return false; }
            
            Map.Entry<K, V> entry = (Map.Entry<K,V>)o;
            K key = entry.getKey();
            
            if (TSB_OAHashtable.this.containsKey(key)) {
                return true;
            }
            return false;
        }
        
        /*
         * Elimina de esta vista (y por lo tanto de la tabla) al par que entra
         * como parámetro (y que debe ser de tipo Entry).
         */
        @Override
        public boolean remove(Object o) {
            if(o == null) { throw new NullPointerException("remove(): parámetro null");}
            if(!(o instanceof Entry)) { return false; }

            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            K key = entry.getKey();
            
            
            if (TSB_OAHashtable.this.remove(key) != null) {
                TSB_OAHashtable.this.count--;
                TSB_OAHashtable.this.modCount++;
                return true;
            }

              return false;
        }     

        @Override
        public int size() {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public void clear() {
            TSB_OAHashtable.this.clear();
        }
        
        private class EntrySetIterator implements Iterator<Map.Entry<K,V>>{

                        
            // índice del elemento actual en el iterador (el que fue retornado 
            // la última vez por next() y será eliminado por remove())...
            private int current_entry;
                        
            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;
            
            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;
            
            /*
             * Crea un iterador comenzando en la primera lista. Activa el 
             * mecanismo fail-fast.
             */

            public EntrySetIterator() {
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }
            
            @Override
            public boolean hasNext() {
                if (TSB_OAHashtable.this.table == null) {
                    return false;
                }
                if (current_entry >= TSB_OAHashtable.this.table.length) {
                    return false;
                }
                return true;
            }

            @Override
            public Entry<K, V> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No quedan elementos por recorrer");
                }
                
                if (TSB_OAHashtable.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("next(): modificacion inesperada de tabla");
                    
                }
                
                current_entry++;
                next_ok = true;
                
                Entry next = (Entry) table[current_entry];
                
                return next;
            }

            @Override
            public void remove() {
                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }
                
                if (TSB_OAHashtable.this.remove(table[current_entry].getKey()) != null) {
                    count--;
                    next_ok=false;
                    modCount++;
                    this.expected_modCount++;
                }
            }            
        }        
    }

    private class ValueCollection extends AbstractCollection<V> {

        @Override
        public Iterator<V> iterator() {
            return new ValueCollectionIterator();
        }

        @Override
        public int size() {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public void clear() {
            TSB_OAHashtable.this.clear();
        }

        @Override
        public boolean contains(Object o) {
            return TSB_OAHashtable.this.containsValue((V)o);
        }

        private class ValueCollectionIterator implements Iterator<V> {

            // índice del elemento actual en el iterador (el que fue retornado 
            // la última vez por next() y será eliminado por remove())...
            private int current_entry;

            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;

            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;

            /*
             * Crea un iterador comenzando en la primera lista. Activa el 
             * mecanismo fail-fast.
             */
            public ValueCollectionIterator() {
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            @Override
            public boolean hasNext() {
                if (TSB_OAHashtable.this.table == null) {
                    return false;
                }
                if (current_entry >= TSB_OAHashtable.this.table.length) {
                    return false;
                }
                return true;}

            @Override
            public V next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No quedan elementos por recorrer");
                }
                
                if (TSB_OAHashtable.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("next(): modificacion inesperada de tabla");
                    
                }

                current_entry++;
                next_ok = true;

                return table[current_entry].getValue();}

            @Override
            public void remove() {
                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }
                count--;
                modCount++;
                expected_modCount++;
                table[current_entry] = new Tombstone();

                next_ok = false;}
        }
    }
    
    public class Tombstone extends Entry<K, V> implements Serializable {

        private Entry entry;

        public Tombstone() {
            super(null, null);
        }
    }
}
