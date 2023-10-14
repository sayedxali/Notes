/**
 * Order: 4
 * <br/>💡 Note: In this way of implementing, we can break the singleton pattern using Cloning!
 */
class CocaCola implements Serializable {

    private static CocaCola cocaColaInstance;

    private CocaCola() {}

    public static CocaCola getCocaColaInstance() {
        if (cocaColaInstance == null) {
            synchronized (CocaCola.class) {
                if (cocaColaInstance == null)
                    cocaColaInstance = new CocaCola();
            }
        }
        return cocaColaInstance;
    }

    // modified here!
    @Serial public Object readResolve() {
        return cocaColaInstance;
    }

}