/**
 * Order 2:<br/>
 * This is the subclass (child class).<br/>
 */
public class WebDeveloper implements Employee {
    @Override
    public int salary() {
        System.out.println("Web development is a bit boring for me y'know :(");
        return 30000;
    }
}
