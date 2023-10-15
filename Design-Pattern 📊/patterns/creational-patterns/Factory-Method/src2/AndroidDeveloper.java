/**
 * Order 2:<br/>
 * This is the subclass (child class).<br/>
 */
public class AndroidDeveloper implements Employee {
    @Override
    public int salary() {
        System.out.println("Iam an Android Developer! (nah I'm not, I'm a java developer ;))");
        return 50000;
    }
}
