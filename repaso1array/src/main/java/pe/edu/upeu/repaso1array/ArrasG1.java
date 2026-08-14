package pe.edu.upeu.repaso1array;

public class ArrasG1 {

    public static int[] ejemplovector(){
        int[] v={15,20,16,12,8};
        System.out.println(v.length);
        v[2]=10;

        return  v;

    }

    public static void imprimevector(int[] v){
        for (int i = 0;  i < v.length; i++ ){
            System.out.print(v[i]+"\t");
        }

    }
public static  void imprimematriz(int [][] m){
        for (int f = 0; f< m.length; f++){
            for (int c = 0; c< m[0].length; c++){
                System.out.print(m[f][c]+"\t");
            }
                System.out.println("");
            }
        }

public static int[][]matrizF5(int tam,int numI){
        int [][]m=new int[tam][tam];
        for (int f = 0;f < m.length;f++){
            for (int c= m[0].length-1;c >=m[0].length-1-f;c--){
              m[f][c]=numI;
              numI++;
            }
        }
        return  m;
}

        public static void main(String[] args){
          //  System.out.println("hola mundo");
           // imprimevector(ejemplovector());
            System.out.println("");
           //int[][] m={{2,3},{4,6}, {9,10}};
            imprimematriz(matrizF5(5,0));
           }
        }

