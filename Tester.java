import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tester {
    public static void main(String[] args) {
        checkProblem5();
        checkProblem4();
        checkProblem3();
        checkProblem2();
        checkProblem();
        /*AVLTree tree=new AVLTree();

        for (int i=1;i<=11;i++){
            tree.insert(i,Integer.toString(i));
        }
        tree.delete(8);
        tree.delete(7);
        tree.delete(3);
        tree.print();

        AVLTree[] splitRes=tree.split(4);
        splitRes[0].print();
        //System.out.println(Arrays.toString(splitRes[0].keysToArray()));
        splitRes[1].print();
        //System.out.println(Arrays.toString(splitRes[1].keysToArray()));*/

    }

    public static void checkInsertRandom(int n, int maxValue) {

        //Case 1
        System.out.println("------------------ CASE 1 ------------------");

        AVLTree tree=new AVLTree();

        // create instance of Random class
        Random rand = new Random();

        for (int i=1;i<=n;i++){
            int rnd = rand.nextInt(maxValue);
            System.out.println("insertion of "+i+": "+tree.insert(i,Integer.toString(i)));
        }

        tree.print();
        System.out.println(Arrays.toString(tree.keysToArray()));
        System.out.println(Arrays.toString(tree.infoToArray()));

        //Case 2
        System.out.println("------------------ CASE 2 ------------------");

        tree=new AVLTree();

        for (int i=1;i<=n;i++){
            System.out.println("insertion of "+i+": "+tree.insert(i,Integer.toString(i)));
        }

        tree.print();
        System.out.println(Arrays.toString(tree.keysToArray()));
        System.out.println(Arrays.toString(tree.infoToArray()));

        //Case 3
        System.out.println("------------------ CASE 3 ------------------");

        tree=new AVLTree();

        for (int i=1;i<=n;i++){
            int rnd = rand.nextInt(5);
            System.out.println("insertion of "+rnd+": "+tree.insert(rnd,Integer.toString(i)));
        }

        tree.print();
        System.out.println(Arrays.toString(tree.keysToArray()));
        System.out.println(Arrays.toString(tree.infoToArray()));
    }

    public static void checkInsertJoin() {

        //Case 1
        System.out.println("------------------ CASE 1 ------------------");

        AVLTree tree1=new AVLTree();

        for (int i=1;i<=5;i++){
            System.out.println("insertion of "+i+": "+tree1.insert(i,Integer.toString(i)));
        }

        tree1.print();
        System.out.println(Arrays.toString(tree1.keysToArray()));
        System.out.println(Arrays.toString(tree1.infoToArray()));


        AVLTree[] splitRes=tree1.split(4);
        splitRes[0].print();
        System.out.println(Arrays.toString(splitRes[0].keysToArray()));
        splitRes[1].print();
        System.out.println(Arrays.toString(splitRes[1].keysToArray()));


        AVLTree tree2=new AVLTree();

        for (int i=7;i<=11;i++){
            System.out.println("insertion of "+i+": "+tree2.insert(i,Integer.toString(i)));
        }

        tree2.print();
        System.out.println(Arrays.toString(tree2.keysToArray()));
        System.out.println(Arrays.toString(tree2.infoToArray()));

        AVLTree tree3=new AVLTree();
        tree3.insert(6,"6");
        tree1.join(tree3.getRoot(),tree2);
        tree1.print();

    }

    public static void checkProblem(){

        System.out.println("============== Before Join=======================");
        AVLTree tree1=new AVLTree();
        tree1.insert(7,"7");
        System.out.println("x=================");
        tree1.print();

        AVLTree tree2=new AVLTree();
        tree2.insert(9,"9");
        tree2.insert(10,"10");
        tree2.insert(6,"6");
        tree2.insert(11,"11");
        System.out.println("t1=================");
        tree2.print();


        AVLTree tree3=new AVLTree();
        tree3.insert(8,"8");
        System.out.println("t2=================");
        tree3.print();

        tree2.join(tree1.getRoot(),tree3);
        System.out.println("============== Join=======================");
        tree2.print();

        System.out.println("============== Split=======================");
        AVLTree[] splitRes=tree1.split(7);
        System.out.println("t2=================");
        splitRes[0].print();
        System.out.println(Arrays.toString(splitRes[0].keysToArray()));
        System.out.println("t1=================");
        splitRes[1].print();
        System.out.println(Arrays.toString(splitRes[1].keysToArray()));
    }

    public static void checkProblem2(){
        System.out.println("============== Before Join=======================");
        AVLTree tree1=new AVLTree();
        System.out.println("x=================");
        tree1.insert(7,"7");
        tree1.print();

        AVLTree tree2=new AVLTree();
        System.out.println("t1=================");
        tree2.insert(9,"9");
        tree2.insert(10,"10");
        tree2.print();

        AVLTree tree3=new AVLTree();
        System.out.println("t2=================");
        tree3.insert(8,"8");
        tree3.print();

        tree3.join(tree1.getRoot(),tree2);
        System.out.println("============== Join=======================");
        tree3.print();

        System.out.println("============== Split=======================");
        AVLTree[] splitRes=tree1.split(7);
        System.out.println("t2=================");
        splitRes[0].print();
        System.out.println(Arrays.toString(splitRes[0].keysToArray()));
        System.out.println("t1=================");
        splitRes[1].print();
        System.out.println(Arrays.toString(splitRes[1].keysToArray()));
    }

    public static void checkProblem4(){
        System.out.println("============== Before Join=======================");
        AVLTree tree1=new AVLTree();
        System.out.println("x=================");
        tree1.insert(5,"5");
        tree1.print();

        AVLTree tree2=new AVLTree();
        System.out.println("t1=================");
        tree2.insert(4,"4");
        tree2.insert(3,"3");
        tree2.print();

        AVLTree tree3=new AVLTree();
        System.out.println("t2=================");
        tree3.insert(6,"6");
        tree3.print();

        tree2.join(tree1.getRoot(),tree3);
        System.out.println("============== Join=======================");
        tree2.print();

        System.out.println("============== Split=======================");
        AVLTree[] splitRes=tree2.split(5);
        System.out.println("t2=================");
        splitRes[0].print();
        System.out.println(Arrays.toString(splitRes[0].keysToArray()));
        System.out.println("t1=================");
        splitRes[1].print();
        System.out.println(Arrays.toString(splitRes[1].keysToArray()));
    }

    public static void checkProblem3(){
        System.out.println("============== Before Join=======================");
        AVLTree tree1=new AVLTree();
        System.out.println("x=================");
        tree1.insert(17,"17");

        AVLTree tree2=new AVLTree();
        tree2.insert(9,"9");
        tree2.insert(6,"6");
        tree2.insert(12,"12");
        tree2.insert(5,"5");
        tree2.insert(7,"7");
        tree2.insert(13,"13");
        tree2.insert(10,"10");
        tree2.insert(11,"11");
        System.out.println("t1=================");
        tree2.print();

        AVLTree tree3=new AVLTree();
        tree2.insert(19,"19");
        tree2.insert(80,"16");
        tree2.insert(22,"22");
        tree2.insert(30,"15");
        tree2.insert(18,"17");
        tree2.insert(23,"23");
        tree2.insert(20,"20");
        tree2.insert(21,"21");
        System.out.println("t2=================");
        tree2.print();


        tree2.join(tree1.getRoot(),tree3);
        System.out.println("============== Join=======================");
        tree2.print();


        System.out.println("============== Split=======================");
        AVLTree[] splitRes=tree2.split(17);
        System.out.println("t2=================");
        splitRes[0].print();
        System.out.println(Arrays.toString(splitRes[0].keysToArray()));
        System.out.println("t1=================");
        splitRes[1].print();
        System.out.println(Arrays.toString(splitRes[1].keysToArray()));
    }

    public static void checkProblem5(){
        AVLTree tree=new AVLTree();

        for (int i=1;i<=24;i++){
            tree.insert(i,Integer.toString(i));
        }
        tree.print();
        AVLTree[] splitRes=tree.split(7);
        System.out.println("t2=================");
        splitRes[0].print();
        System.out.println(Arrays.toString(splitRes[0].keysToArray()));
        System.out.println("t1=================");
        splitRes[1].print();
        System.out.println(Arrays.toString(splitRes[1].keysToArray()));
    }

    public static void checkProblem6(){
        AVLTree tree1=new AVLTree();

        tree1.insert(12,"a");
        tree1.insert(10,"a");
        tree1.insert(14,"a");
        tree1.insert(13,"a");
        tree1.insert(11,"a");
        tree1.insert(15,"a");
        tree1.insert(9,"a");
        tree1.insert(8,"a");
        tree1.print();

        AVLTree tree2=new AVLTree();

        tree2.insert(20,"a");
        tree2.insert(18,"a");
        tree2.insert(22,"a");
        tree2.insert(17,"a");
        tree2.insert(23,"a");
        tree2.insert(21,"a");
        tree2.insert(19,"a");
        tree2.insert(24,"a");
        tree2.print();

        AVLTree root=new AVLTree();
        root.insert(16,"k");

        tree1.join(root.getRoot(),tree2);
        tree1.print();

    }


    public static void checkProblem7(){
        AVLTree tree1=new AVLTree();

        tree1.insert(12,"a");
        tree1.insert(10,"a");
        tree1.print();

        AVLTree tree2=new AVLTree();

        tree2.insert(20,"a");
        tree2.insert(18,"a");
        tree2.print();

        AVLTree root=new AVLTree();
        root.insert(16,"k");

        tree1.join(root.getRoot(),tree2);
        tree1.print();

    }
}
