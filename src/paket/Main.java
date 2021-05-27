package paket;

import java.awt.ActiveEvent;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		
		
		 try {
		      File myObj = new File("engel.txt");
		      if (myObj.createNewFile()) {
		        System.out.println("File created: " + myObj.getName());
		      } else {
		        System.out.println("File already exists.");
		      }
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		


 FileWriter myWriter = new FileWriter("engel.txt");

ArrayList<String> matris_engelleri=new ArrayList<>();

int random_i,random_j;
Random rand = new Random();
while(matris_engelleri.size()<750) {
	
	
	random_i=rand.nextInt(50);
	random_j=rand.nextInt(50);
	System.out.println(random_i+","+random_j+",k"+"\n");
	
String engel=random_i+","+random_j;
if(!matris_engelleri.contains(engel)) {
	matris_engelleri.add(engel);
	   try {
		      
		      myWriter.write(random_j+","+random_i+",k"+"\n");
		      
		      System.out.println("Successfully wrote to the file.");
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	
}
	
}
	myWriter.close();	
		
	JFrame frame=new JFrame("Yol planlama");
    frame.setSize(1000,1000);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(false);
    frame.setContentPane(new arayüz(matris_engelleri));
    //frame.getContentPane().setBackground(Color.BLACK);
    frame.setVisible(true);
	
	
	
	}
	
	
	
}




class arayüz extends JPanel implements ActiveEvent,ActionListener,MouseListener  {
	
	boolean baslangic_secildi=false;
	boolean bitis_secildi=false;
	int start_x=0;
	int start_y=0;
	int finish_x=0;
	int finish_y=0;
	ArrayList<String>engeller;
	 final double gamma = 0.8;
	 int[][] maze=new int[50][50];
	 int[] ana_maze=new int[2500];
	 int[][] R=new int[2500][2500];       // Reward lookup
	 double[][] Q=new double[2500][2500];
	 int gecis_odulu=3;
	 int bitis_odulu_5;
	 Button b=new Button("R yi hesapla"); 
	 Button b_2=new Button("Q yi hesapla"); 
	 Button b_3=new Button("karakteri oynat"); 
	 Button b_4=new Button("karakterle Q yu ogren"); 
	 Button b_5=new Button("kazanc/maliyet grafigi");
	 Button b_6=new Button("bölüm adým sayýsý grafigi");
	 ArrayList<Integer>maliyet=new ArrayList<>();
	 ArrayList<Integer>adim_sayisi=new ArrayList<>();
	 ArrayList<String>yolu_boya=new ArrayList<>();
	 Timer zamanlama=new Timer(500,this);
	 Timer zamanlama_2=new Timer(1,this);
	 boolean q_hesaplandi=false;
	 boolean karakteri_oynat=false;
	 boolean karakteri_oynat_2=false;
	 boolean maliyeti_goster=false;
	 boolean adimi_goster=false;
	 boolean karakteri_durdur=false;
	 int karakter_konumx=0;
	 int karakter_konumy=0;
	 int duvar=-3;
	 int onceki_konum=0;
	public arayüz(ArrayList<String>engeller) {
		
		this.engeller=engeller;
		addMouseListener(this);
		 
		    //2nd step  
		 b.addActionListener(this); 
		 add(b);
		 b_2.addActionListener(this); 
		 add(b_2);
		 b_3.addActionListener(this); 
		 add(b_3);
		 b_4.addActionListener(this); 
		 add(b_4);
		 b_5.addActionListener(this); 
		 add(b_5);
		 b_6.addActionListener(this); 
		 add(b_6);
		
		for(int i=0;i<50;i++) {
			for(int j=0;j<50;j++) {
				
				maze[i][j]=0;
				ana_maze[50*i+j]=0;
			}
		}
		
		 for(String engel:engeller) {
			  
			 maze[Integer.parseInt(engel.split(",")[0])][Integer.parseInt(engel.split(",")[1])]=-1;
	        ana_maze[50*Integer.parseInt(engel.split(",")[1])+Integer.parseInt(engel.split(",")[0])]=-1;
			  
		  }
		 
	
			
		 
	
					 
			 
			
		 zamanlama.start();
		 zamanlama_2.start();
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getSource()==b) {
			
			R_hesapla();
		}
		
		if(e.getSource()==b_2 &&q_hesaplandi==false) {
			
			Q_hesapla();
			q_hesaplandi=true;
		}
		
		if(e.getSource()==b_3)
			karakteri_oynat=true;
		
		if(e.getSource()==b_4)
			karakteri_oynat_2=true;
		
		if(e.getSource()==b_5) {
			maliyeti_goster=true;
			adimi_goster=false;
		repaint();	
		}
		
		if(e.getSource()==b_6) {
			adimi_goster=true;
			maliyeti_goster=false;
		repaint();	
		}
		
		if(e.getSource()==zamanlama&& karakteri_oynat==true) {
			  System.out.println("-----------------");
			
			int random_durum=karakter_konumy*50+karakter_konumx;	
	
             
               int siradaki_durum =maxolasýDurum(random_durum);
            
          
               karakter_konumy=siradaki_durum/50;
               karakter_konumx=siradaki_durum%50;
               
			repaint();
			if((karakter_konumx+karakter_konumy*50)==(finish_x+finish_y*50)) {
				karakteri_oynat=false;
			     karakteri_durdur=true;	
			}
		
			
		}
			
		
		if(e.getSource()==zamanlama_2&& karakteri_oynat_2==true) {
			  System.out.println("-----------------");
			int baslangic_durum=start_x+start_y*50;
			int random_durum=karakter_konumy*50+karakter_konumx;	
	   
			int final_durum=finish_y*50+finish_x;
			Random rand=new Random();
			boolean girdi=false;
			int k=0;
			
			System.out.println(random_durum+" "+final_durum);
			if(Q[baslangic_durum][baslangic_durum+1]==0.0||Q[baslangic_durum][baslangic_durum-1]==0.0) {
			
				//int random_durum=rand.nextInt(2500);
				
				
				
				//&&random_durum!=  maze[random_durum/50][randomdurum%50]!=-1;
				
				if (random_durum!=final_durum) {
				
					
					girdi=true;
	                int[] olasý_durumlar = olasýDurumlar(random_durum);

	                // Pick a random action from the ones possible
	                int index = rand.nextInt(olasý_durumlar.length);
	                int siradaki_durum = olasý_durumlar[index];

	                // Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
	                //double q = Q[random_durum][nextState];
	                double maxQ = maxQ(siradaki_durum);
	               

	                double value=R[random_durum][siradaki_durum]+(0.8)*maxQ;
	              
	                Q[random_durum][siradaki_durum] = value;
	                
	            
	                if(maze[siradaki_durum/50][siradaki_durum%50]==-1) {  // if clause yeni eklenio
	               
	                
	                	siradaki_durum=baslangic_durum;
	                  
	                }
	                random_durum = siradaki_durum;
	                karakter_konumx=random_durum%50;
	                karakter_konumy=random_durum/50;
	             
	            }
				else {
				random_durum=start_y*50+start_x;
			    karakter_konumx=start_x;
			    karakter_konumy=start_y;
				}
			
			
			}else
				karakteri_oynat_2=false;
             repaint();
			
		}
		
		
		
	}

	@Override
	public void dispatch() {
		// TODO Auto-generated method stub
		
	}
	
	
	protected void paintComponent(Graphics g){
		
		super.paintComponent(g);
	 
		    b.setBounds(850,100,100,30);  
		    b_2.setBounds(850,150,100,30);  
		    b_3.setBounds(850,200,100,30);  
		    b_4.setBounds(850,250,100,30);
		    b_5.setBounds(850,300,100,30);
		    b_6.setBounds(830,350,150,30);
		    //2nd step  
		   if(maliyeti_goster==false && adimi_goster==false) {
		   
	    g.setColor(Color.black);
        
        for(int i=0;i<=50;i++)
	       g.drawLine(16*i, 0,16*i,800);
        for(int i=0;i<=50;i++)
 	   g.drawLine(0,i*16,800,16*i);
   	   
        g.setColor(Color.blue);
        
        if(baslangic_secildi==true) {
        	
        	System.out.println(start_x);
        	 g.fillRect(16*start_x,16*start_y,16,16);        	
        }
		
        g.setColor(Color.green);
        
  if(bitis_secildi==true) {
        	
        	System.out.println(finish_x);
        	 g.fillRect(16*finish_x,16*finish_y,16,16);        	
        }
  
  g.setColor(Color.red);
  for(String engel:engeller) {
	  
	  
	  
	  g.fillRect(16*Integer.parseInt(engel.split(",")[1]),16*Integer.parseInt(engel.split(",")[0]),16,16); 
	  
  }
 
  
  if(karakteri_oynat==true) {
	  g.setColor(Color.yellow);
	  for(int i=0;i<yolu_boya.size();i++)
		  g.fillRect(16*Integer.parseInt(yolu_boya.get(i).split(",")[0]),16*Integer.parseInt(yolu_boya.get(i).split(",")[1]),16,16);
 g.setColor(Color.magenta);
 g.fillRect(16*karakter_konumx,16*karakter_konumy,16,16);
 yolu_boya.add(karakter_konumx+","+karakter_konumy);
  
  }
  
  if(karakteri_durdur==true) {
	  g.setColor(Color.yellow);
	  for(int i=0;i<yolu_boya.size();i++)
		  g.fillRect(16*Integer.parseInt(yolu_boya.get(i).split(",")[0]),16*Integer.parseInt(yolu_boya.get(i).split(",")[1]),16,16);

	  g.setColor(Color.magenta);
	  g.fillRect(16*karakter_konumx,16*karakter_konumy,16,16); 
	   
	   }
        
  if(karakteri_oynat_2==true) {
 g.setColor(Color.magenta);
 g.fillRect(16*karakter_konumx,16*karakter_konumy,16,16); 
  
  }
  
	
  
  
	}//maliyeti goster 
  
  if(maliyeti_goster==true) {
	  
	  /*
	  Graphics2D g2d = (Graphics2D) g;
	  /*
	  g2d.setColor(Color.RED);
	  for(int i=0;i<=800;i++)
	       g2d.drawLine(i*2, 0,i*2,1200); //dikey
       for(int i=0;i<=800;i++)
	   g2d.drawLine(0,i*2,1200,i*2);//yatay
	  
	  */
	  
	  /*
	  int prevX=100;
     
      int unitX =2;

	  g2d.setColor(Color.green);
	  g2d.drawLine(0,500, 2000,500);
      g2d.setColor(Color.black);
      for (int x=0;x<maliyet.size()-1;x++) {
          g2d.drawLine(prevX, 500-maliyet.get(x),prevX+unitX,500-maliyet.get(x+1));
          prevX+=unitX;
      }
      
      Graphics2D g2d = (Graphics2D) g;
	  /*
	  g2d.setColor(Color.RED);
	  for(int i=0;i<=800;i++)
	       g2d.drawLine(i*2, 0,i*2,1200); //dikey
       for(int i=0;i<=800;i++)
	   g2d.drawLine(0,i*2,1200,i*2);//yatay
	  
	  */
	  Graphics2D g2d = (Graphics2D) g;
	  ArrayList<Integer>maliyet_grafik=new ArrayList<>();
	  int prevX=100;
     int max=Collections.max(maliyet);
      int unitX =5;
int carpan=max/400;
	  g2d.setColor(Color.green);
	  g2d.drawLine(0,500, 2000,500);
      g2d.setColor(Color.black);
      System.out.println(maliyet.size());
  
      for (int x=0;x<maliyet.size();x++) {
    	  if(x%20==0)
    	  g.drawString(String.valueOf(x),prevX, 515);
    	  maliyet_grafik.add(maliyet.get(x)/carpan);
    
          g2d.drawLine(prevX, 500,prevX,500-maliyet_grafik.get(x));
          prevX+=unitX;
      }
      
      int sayý=(max/10);
      int sayi_1=sayý;
      for(int x=1;x<=10;x++) {
    	 
    	  g.drawString(String.valueOf(sayi_1),10,500-40*x);
      sayi_1+=sayý;
      }
      
      
      
      
  }
	  
  
  if(adimi_goster==true) {
	  
	  
	  Graphics2D g2d = (Graphics2D) g;
	  /*
	  g2d.setColor(Color.RED);
	  for(int i=0;i<=800;i++)
	       g2d.drawLine(i*2, 0,i*2,1200); //dikey
       for(int i=0;i<=800;i++)
	   g2d.drawLine(0,i*2,1200,i*2);//yatay
	  
	  */
	  ArrayList<Integer>adim_sayisi_grafik=new ArrayList<>();
	  int prevX=100;
     int max=Collections.max(adim_sayisi);
      int unitX =5;
int carpan=max/400;
	  g2d.setColor(Color.green);
	  g2d.drawLine(0,500, 2000,500);
      g2d.setColor(Color.black);
      System.out.println(adim_sayisi.size());
     
      for (int x=0;x<adim_sayisi.size();x++) {
    	  if(x%20==0)
    	  g.drawString(String.valueOf(x),prevX, 515);
    	  adim_sayisi_grafik.add(adim_sayisi.get(x)/carpan);
    
          g2d.drawLine(prevX, 500,prevX,500-adim_sayisi_grafik.get(x));
          prevX+=unitX;
      }
      
      int sayý=(max/10);
      int sayi_1=sayý;
      for(int x=1;x<=10;x++) {
    	 
    	  g.drawString(String.valueOf(sayi_1),10,500-40*x);
      sayi_1+=sayý;
      }
  }
  
	  
  
}
	
	
	public void R_hesapla() {
		
		
		 
		 for (int k = 0; k < 2500; k++) {

        
           int  i = k / 50;
            int  j = k%50;

            
            for (int s = 0; s < 2500; s++) {
                R[k][s] = -5;
                Q[k][s]=0;
            }


            if (true) {
            	
            	
            	
                int yukari = i - 1;
                if (yukari >= 0) {
                    int gidilecek_kare = yukari * 50 + j;
                    if (maze[yukari][j] == 0) {
                        R[k][gidilecek_kare] = 3;
                    } else if (maze[yukari][j] == 5) {
                        R[k][gidilecek_kare] = 100;
                    } else {
                        R[k][gidilecek_kare] = -5;
                    }
                }

         
                int asagi = i + 1;
                if (asagi < 50) {
                    int gidilecek_kare = asagi * 50 + j;
                    if (maze[asagi][j] == 0) {
                        R[k][gidilecek_kare] = 3;
                    } else if (maze[asagi][j] == 5) {
                        R[k][gidilecek_kare] = 100;
                    } else {
                        R[k][gidilecek_kare] = -5;
                    }
                }

             
                int solagit = j - 1;
                if (solagit >= 0) {
                    int gidilecek_kare = i * 50 + solagit;
                    if (maze[i][solagit] == 0) {
                        R[k][gidilecek_kare] = 3;
                    } else if (maze[i][solagit] == 5) {
                        R[k][gidilecek_kare] = 100;
                    } else {
                        R[k][gidilecek_kare] = -5;
                    }
                }


                int sagagit = j + 1;
                if (sagagit < 50) {
                    int gidilecek_kare = i * 50 + sagagit;
                    if (maze[i][sagagit] == 0) {
                        R[k][gidilecek_kare] = 3;
                    } else if (maze[i][sagagit] == 5) {
                        R[k][gidilecek_kare] = 100;
                    } else {
                        R[k][gidilecek_kare] = -5;
                    }
                }

          
                
                int solust_i=i-1;
                int solust_j=j-1;
                if (solust_i>0&& solust_j>0) {
                    int gidilecek_kare = solust_i * 50 + solust_j;
                    if (maze[solust_i][solust_j] == 0) {
                        R[k][gidilecek_kare] = 3;
                    } else if (maze[solust_i][solust_j] == 5) {
                        R[k][gidilecek_kare] = 100;
                    } else {
                        R[k][gidilecek_kare] = -5;
                    }
                }
                
                int sagust_i=i-1;
                int sagust_j=j+1;
                if (sagust_i>0&& sagust_j<50) {
                    int gidilecek_kare = sagust_i * 50 + sagust_j;
                    if (maze[sagust_i][sagust_j] == 0) {
                        R[k][gidilecek_kare] = 3;
                    } else if (maze[sagust_i][sagust_j] == 5) {
                        R[k][gidilecek_kare] = 100;
                    } else {
                        R[k][gidilecek_kare] = -5;
                    }
                }
                
             
                int saðalt_i=i+1;
                int saðalt_j=j+1;
                if (saðalt_i<50 && saðalt_j<50) {
                    int gidilecek_kare = saðalt_i * 50 + saðalt_j;
                    if (maze[saðalt_i][saðalt_j] == 0) {
                        R[k][gidilecek_kare] = 3;
                    } else if (maze[saðalt_i][saðalt_j] == 5) {
                        R[k][gidilecek_kare] = 100;
                    } else {
                        R[k][gidilecek_kare] = -5;
                    }
                }
                
                
                int solalt_i=i+1;
                int solalt_j=j-1;
                if (solalt_i<50&& solalt_j>0) {
                    int gidilecek_kare = solalt_i * 50 + solalt_j;
                    
                    if (maze[solalt_i][solalt_j] == 0) {
                        R[k][gidilecek_kare] = 3;
                    } else if (maze[solalt_i][solalt_j] == 5) {
                        R[k][gidilecek_kare] = 100;
                    } else {
                        R[k][gidilecek_kare] = -5;
                    }
                    
                   
                    
                }
                
                
                
         
                
                
            }
        }
		 
		

		 
	     for (int i = 0; i < 2500; i++){
	            for(int j = 0; j < 2500; j++){
	                Q[i][j] =0;
	            }
	        }
	     System.out.println("R hesaplandi");
		
	}

	 public int[] olasýDurumlar(int random_durum) {
	
		  ArrayList<Integer> sonuclar = new ArrayList<>();
	        for (int i = 0; i < 2500; i++) {
	           if ( Q[random_durum][i] >=0 &&(random_durum%50!=49 &&i==random_durum+1)||(random_durum%50!=0 && i==random_durum-1)||i==random_durum+50||i==random_durum-50||i==random_durum-51||i==random_durum-49||i==random_durum+49||i==random_durum+51) { //R[random_durum][i] != -5   Q[random_durum][i] >=0  	i=random_durum+1//random_durum-1//random_durum+50//random_durum-50//random_durum-51 -49 +49 +51
	               
	        	   if((i==random_durum+1 && i%50==0)||(i==random_durum-1&&i%50==49)||(i==random_durum-51&&i%50==49)||(i==random_durum-49&&i%50==0)||(i==random_durum+49&&i%50==49)||(i==random_durum+51&&i%50==0))
	        		   continue;
	        	   
	        	   sonuclar.add(i);
	            }
	        }

	        return sonuclar.stream().mapToInt(Integer::intValue).toArray();
	}
	 
	 public int maxolasýDurum(int random_durum) {
			
		 int[] olasý_durumlar = olasýDurumlar(random_durum);
         ArrayList<Double> olasý_durumlar_en_yuksek=new ArrayList<>();
	     int maxAt = 0;

	     for (int i = 0; i < olasý_durumlar.length; i++) {
	    	 olasý_durumlar_en_yuksek.add(Q[random_durum][olasý_durumlar[i]]);
	    	 System.out.print(Q[random_durum][olasý_durumlar[i]]+"  ");

	     }
	     maxAt=olasý_durumlar_en_yuksek.indexOf(Collections.max(olasý_durumlar_en_yuksek));
	     
           // Pick a random action from the ones possible
      
           int siradaki_durum = olasý_durumlar[maxAt];

	      return siradaki_durum;
	}

	
	  double maxQ(int siradaki_durum) {
	        int[] actionsFromState =olasýDurumlar(siradaki_durum);
	        //the learning rate and eagerness will keep the W value above the lowest reward
	        double maxValue = -10.0;
	        for (int nextAction : actionsFromState) {
	            double value = Q[siradaki_durum][nextAction];

	            if (value > maxValue)
	                maxValue = value;
	        }
	        return maxValue;
	    }
	
	
	public void Q_hesapla() {
		
	
		int final_durum=finish_y*50+finish_x;
		Random rand=new Random();
		boolean girdi=false;
		int k=0;
		int dur=0;
		int random_durum=start_y*50+start_x;
		System.out.println(random_durum+" "+final_durum);
		while(dur++ <100) {
		
			//int random_durum=rand.nextInt(2500);
			int adým_sayýsý=0;
			int maliyet_1=0;
			
			
			//&&random_durum!=  maze[random_durum/50][randomdurum%50]!=-1;
			
			while (random_durum!=final_durum) {
			
				
				girdi=true;
                int[] olasý_durumlar = olasýDurumlar(random_durum);

              
                int index = rand.nextInt(olasý_durumlar.length);
                int siradaki_durum = olasý_durumlar[index];

              
                double maxQ = maxQ(siradaki_durum);
               

                double value=R[random_durum][siradaki_durum]+(0.9)*maxQ;
              
                Q[random_durum][siradaki_durum] = value;
                
             
                if(maze[siradaki_durum/50][siradaki_durum%50]==-1) {  // if clause yeni eklenio
               
                //baslangic_durum random_durum
                	siradaki_durum=random_durum;
                	maliyet_1-=5;
                    adým_sayýsý++;
                }
                else
                	maliyet_1+=3;
                random_durum = siradaki_durum;
                
             adým_sayýsý++;
            }
			maliyet_1+=5;
		    maliyet.add(maliyet_1);
			adim_sayisi.add(adým_sayýsý);
			adým_sayýsý=0;
			random_durum=start_y*50+start_x;
		
		/*
		if(Q[random_durum][random_durum+1]>0.0||Q[random_durum][random_durum-1]>0.0||Q[random_durum][random_durum-50]>0.0||Q[random_durum][random_durum+50]>0.0||Q[random_durum][random_durum-49]>0.0||Q[random_durum][random_durum-51]>0.0||Q[random_durum][random_durum+51]>0.0||Q[random_durum][random_durum+49]>0.0)
		break;
		*/
		}
	System.out.println("Q Hesaplandi");		
	
	}
		

	@Override
	public void mouseClicked(MouseEvent e) {
		
	
		
		if(baslangic_secildi==true&&bitis_secildi==false) {
			 finish_x=e.getX()/16;
			 finish_y=e.getY()/16;
			bitis_secildi=true;
			
			 maze[finish_y][finish_x]=5;
			
			try {
				FileWriter myWriter = new FileWriter("engel.txt",true);
				  myWriter.write(finish_x+","+finish_y+",y"+"\n");
					myWriter.close();	
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("sa");
				e1.printStackTrace();
			}
			
			repaint();
		}
		
		if(baslangic_secildi==false) {
			
			 start_x=e.getX()/16;
			 start_y=e.getY()/16;
			baslangic_secildi=true;
			karakter_konumx=start_x;
			karakter_konumy=start_y;
			
			try {
				FileWriter myWriter = new FileWriter("engel.txt",true);
				  myWriter.write(start_x+","+start_y+",m"+"\n");
					myWriter.close();	
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("sa");
				e1.printStackTrace();
			}
			
			repaint();
		}
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	


}