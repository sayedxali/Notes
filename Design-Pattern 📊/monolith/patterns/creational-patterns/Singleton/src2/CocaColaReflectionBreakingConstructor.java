/**
 * Order: 2
 * <br/>Fixed: We cannot break singleton pattern with <code>Reflection</code> API.
 * <br/>💡 Note: In this way of implementing, we can break the singleton pattern using Colning & Deserializing!
 */
class CocaCola {

    private static CocaCola cocaColaInstance;

    // modified here!
    private CocaCola() {
        if (cocaColaInstance != null)
            throw new RuntimeException("😝 You cannot break singleton pattern by using `Reflection` API!")
    }

    public static CocaCola getCocaColaInstance() {
        // synchronized; only one thread will access (for making this method thread-safe)
        if (cocaColaInstance == null) {
            synchronized (CocaCola.class) {
                if (cocaColaInstance == null)
                    cocaColaInstance = new CocaCola();
            }
        }
        return cocaColaInstance;
    }

}