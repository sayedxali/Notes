/**
 * Order: 5
 * We're calling the super() method of clone so that we can clone the object as it is and recreate the singleton breaking.
 * Note: In this way of implementing, we can break the singleton pattern using Colning!
 */
class CocaCola implements Serializable, Cloneable { // modified here!

    private static CocaCola cocaColaInstance;

    private CocaCola() {
    }

    public static CocaCola getCocaColaInstance() {
        if (cocaColaInstance == null) {
            synchronized (CocaCola.class) {
                if (cocaColaInstance == null)
                    cocaColaInstance = new CocaCola();
            }
        }
        return cocaColaInstance;
    }

    @Serial
    public Object readResolve() {
        return cocaColaInstance;
    }

    // modified here!
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}