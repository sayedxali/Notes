/**
 * Order: 6
 * We're calling the same object instance so that we can prevent from object getting cloned and creating a new object out of it.
 * Note: In this way of implementing, we CANNOT break the singleton pattern!
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
        return cocaColaInstance; // <------------------------------modified specifically here!
    }

}