/**
 * Order: 3
 * <br/>Fixed: We cannot break singleton pattern with <code>Reflection</code> API.
 * <br/>💡 Note: In this way of implementing, we can break the singleton pattern using Colning & Deserializing!
 */
enum CocaCola {
    INSTANCE;

    public void cola() {
        System.out.println("I am testing whether singleton for first instance works or not!");
    }

}