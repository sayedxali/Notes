/**
 * Order: 1
 * <br/>💡 Note: In this way of implementing, we can break the singleton pattern using Reflection, Colning & Deserializing!
 */
class CocaCola {

    private static CocaCola cocaColaInstance;

    private CocaCola() {
    } // make the constructor private

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