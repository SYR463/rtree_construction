//  https://github.com/davidmoten/rtree

package ARPN4ITS;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.SplitterRStar;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;

import java.io.IOException;

import static com.github.davidmoten.rtree.geometry.Geometries.point;

public class Main {
    public static void main(String[] args) throws IOException {

        SplitterRStar splitterRStar = new SplitterRStar();
        RTree<Object, Geometry> rStarTree = RTree.minChildren(2).maxChildren(8).splitter(splitterRStar).create();



        RTree<String, Point> tree = RTree.maxChildren(3).create();
        tree = tree.add("R1", point(4, 5))
                .add("R2", point(1, 4))
                .add("R3", point(3, 3))
                .add("R4", point(2, 2))
                .add("R5", point(4, 1));



//        Observable<Entry<String, Point>> entries =
//                tree.search(Geometries.rectangle(8, 15, 30, 35));

        System.out.println(tree.asString());

        tree.visualize(600, 600).save("target/mytree.png");



        /* // 保存图像
        Visualizer visualize = tree.visualize(5, 5);
        visualize.save("saved_image.png");*/

        /* // 保存图像
        BufferedImage image = visualize.createImage();
        File outputFile = new File("saved_image.png");
        ImageIO.write(image, "png", outputFile);*/
    }
}