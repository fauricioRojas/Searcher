package gui;

import com.sun.management.OperatingSystemMXBean;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanServerConnection;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import searcher.Facilitator;
import searcher.ExecutionInformation;
import searcher.Parallel.searchParallel;
import searcher.SearchInformation;
import searcher.Sequential.searchSequential;
import searcher.WordsInformation;

public class Browser extends javax.swing.JFrame {
    private Statistics myStatistics;
    private Facilitator myFacilitator;
    private searchSequential mySequential;
          
    private MBeanServerConnection mbsc;
    
    /**
     * Creates new form browser
     */
    public Browser() {
        initComponents();
        setLocationRelativeTo(null);
        this.myFacilitator = new Facilitator(this);
        this.myStatistics = new Statistics();
        this.mySequential = new searchSequential(myFacilitator);
        
        this.mbsc = ManagementFactory.getPlatformMBeanServer();
        
        setMargin();
        selectSequential();
        setHyperlinkInTextResults();
    }
    
    /**
     * This method set margin to the text input
     */
    public void setMargin() {
        Border border = BorderFactory.createLineBorder(textAreaPages.getBackground());
        textAreaPages.setBorder(BorderFactory.createCompoundBorder(border, 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        textAreaResults.setBorder(BorderFactory.createCompoundBorder(border, 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        border = BorderFactory.createLineBorder(java.awt.Color.GRAY);
        textSearch.setBorder(BorderFactory.createCompoundBorder(border, 
            BorderFactory.createEmptyBorder(0, 5, 0, 5)));        
    }
    
    /**
     * This method change the color of the type execution's type buttons
     */
    public void selectSequential() {
        buttonSequential.setBackground(new java.awt.Color(0, 178, 118));
        buttonParallel.setBackground(new java.awt.Color(231, 76, 60));
    }
    
    /**
     * This method set the hyperlink to the web site in the text result
     */
    public void setHyperlinkInTextResults() {
        textAreaResults.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.browse(e.getURL().toURI());
                    } catch (URISyntaxException | IOException ex) {
                    }
                }
            }
        });  
    }
    
    /**
     * This method change the color of the type execution's type buttons
     */
    public void selectParallel() {
        buttonSequential.setBackground(new java.awt.Color(231, 76, 60));
        buttonParallel.setBackground(new java.awt.Color(0, 178, 118));
    }
    
    /**
     * This method set a text in textResults
     * @param text Text to set in textResults
     */
    public void setTextInTextResults(String text) {
        textAreaResults.setText(text);
    }
           
    private void createChartExecutionTime(double Sequential, double Parallel)
    {   
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(Sequential , "Sequential" , "");
        dataset.addValue(Parallel , "Parallel" , ""); 

        JFreeChart barChart = ChartFactory.createBarChart("Execution's time","Execution's type",
                "Time",dataset,PlotOrientation.VERTICAL, true, false, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        myStatistics.container.remove(0);
        myStatistics.container.add(chartPanel,"Execution's time",0);
        parallelTime = 0;
        sequentialTime = 0;
    }
   
    private void createChartUseCPU(long Sequential, long Parallel)
    {   
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(Sequential , "Sequential" , "");
        dataset.addValue(Parallel , "Parallel" , ""); 

        JFreeChart barChart = ChartFactory.createBarChart("CPU use time","Execution's type",
                "Percents",dataset,PlotOrientation.VERTICAL, true, false, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        myStatistics.container.remove(1);
        myStatistics.container.add(chartPanel,"CPU usage time",1);
        percentParallel = 0;
        percentSequential = 0;
    }
   
    private void createChartComparison()
    { 
        final XYSeries Sequential = new XYSeries("Sequential");
        final XYSeries Parallel = new XYSeries("Parallel");

        for(ExecutionInformation info : ExecutionInformation.info)
        {
            if(info.type.equals("Sequential"))
                Sequential.add(info.i,info.time);
            else
                Parallel.add(info.i,info.time);
        } 

        final XYSeriesCollection dataset = new XYSeriesCollection();          
        dataset.addSeries( Sequential );          
        dataset.addSeries( Parallel );    

        JFreeChart xylineChart = ChartFactory.createXYLineChart("Comparison","Execution's number",
                "Time",dataset,PlotOrientation.VERTICAL,true , true , false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);

        final XYPlot plot = xylineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0 , Color.RED);
        renderer.setSeriesPaint(1 , Color.BLUE);

        renderer.setSeriesStroke(0 , new BasicStroke(3.0f));
        renderer.setSeriesStroke(1 , new BasicStroke(3.0f));
        plot.setRenderer(renderer); 

        myStatistics.container.remove(2);
        myStatistics.container.add(chartPanel,"Comparison",2);
    }
   
    private void createChartAppearances()
    {   
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for(WordsInformation info : WordsInformation.info)
        {
            dataset.addValue(info.i, "", info.word);
        }
        JFreeChart barChart = ChartFactory.createBarChart("Appearances","Words","Appearances",
                dataset,PlotOrientation.VERTICAL, false, false, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        myStatistics.container.remove(3);
        myStatistics.container.add(chartPanel,"Appearances",3);
    }
   
    private double sequentialTime = 0;
    private double parallelTime = 0;
    private int consecutiveParallel = 0;
    private int consecutiveSequential = 0;
    long percentSequential = 0;
    long percentParallel = 0;
    
    /**
     * This method is the principal, handles of call the others methods necessary to the search
     * @throws IOException 
     */
    public void search() throws IOException {
        if(!textSearch.getText().equals("") && !textAreaPages.getText().equals("")) {      
            WordsInformation.info.clear();
            if(buttonSequential.getBackground().getRed() == 0) { // Sequential
                consecutiveSequential++;
                try {
                    ArrayList<String> arrayWords = myFacilitator.getWordsToSearch();
                    ArrayList<String> webSites = myFacilitator.getWebSites();
                    
                    OperatingSystemMXBean osMBean = ManagementFactory.newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
                    long nanoBefore = System.nanoTime();
                    long cpuBefore = osMBean.getProcessCpuTime();
                
                    double time = System.currentTimeMillis();
                    ArrayList<SearchInformation> arrayInformation = this.mySequential.searchSequential(arrayWords, webSites);
                    double totalTime = (System.currentTimeMillis() - time)/1000;
                    
                    long cpuAfter = osMBean.getProcessCpuTime();
                    long nanoAfter = System.nanoTime();

                    
                    if (nanoAfter > nanoBefore)
                     percentSequential = ((cpuAfter-cpuBefore)*100L)/(nanoAfter-nanoBefore);
                    else 
                        percentSequential = 0;
                    
                    if(arrayInformation.size() > 0){
                        myFacilitator.showResults(arrayInformation, totalTime,false,consecutiveSequential);
                        sequentialTime = totalTime;
                        if(parallelTime != 0)
                        {
                            createChartExecutionTime(sequentialTime, parallelTime);
                            createChartUseCPU(percentSequential, percentParallel);
                        }
                    }
                    else {
                        setTextInTextResults("<div style='font-family: Arial, Helvetica, sans-serif; font-size: 12px;'>No results found.</div>");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Browser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else { // Parallel
                consecutiveParallel++;
                searchParallel search_parallel = new searchParallel(myFacilitator.getWordsToSearch(), myFacilitator.getWebSites());
                
                OperatingSystemMXBean osMBean = ManagementFactory.newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
                long nanoBefore = System.nanoTime();
                long cpuBefore = osMBean.getProcessCpuTime();
                
                double time = System.currentTimeMillis();                
                ArrayList<SearchInformation> arrayInformation = search_parallel.search();
                double totalTime = (System.currentTimeMillis() - time)/1000;
                
                long cpuAfter = osMBean.getProcessCpuTime();
                long nanoAfter = System.nanoTime();
                
                if (nanoAfter > nanoBefore)
                    percentParallel = ((cpuAfter-cpuBefore)*100L)/(nanoAfter-nanoBefore);
                else 
                    percentParallel = 0;
                
                if(arrayInformation.size() > 0){
                    myFacilitator.showResults(arrayInformation, totalTime,true,consecutiveParallel);
                    parallelTime = totalTime;
                    if(sequentialTime != 0)
                    {
                        createChartExecutionTime(sequentialTime, parallelTime);   
                        createChartUseCPU(percentSequential, percentParallel);
                    }
                }
                else {
                    setTextInTextResults("<div style='font-family: Arial, Helvetica, sans-serif; font-size: 12px;'>No results found.</div>");
                }    
            }            
        }
        else {
            setTextInTextResults("<div style='font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: red;'>Please enter web sites and words to search.</div>");
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        buttonSequential = new javax.swing.JButton();
        buttonParallel = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaPages = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAreaResults = new javax.swing.JTextPane();
        jPanel6 = new javax.swing.JPanel();
        buttonStatistics = new javax.swing.JButton();
        buttonSearch = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        textSearch = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Web searcher");
        setResizable(false);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Select the execution's type");

        buttonSequential.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        buttonSequential.setText("Sequential");
        buttonSequential.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonSequential.setPreferredSize(new java.awt.Dimension(150, 30));
        buttonSequential.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSequentialActionPerformed(evt);
            }
        });

        buttonParallel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        buttonParallel.setText("Parallel");
        buttonParallel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonParallel.setPreferredSize(new java.awt.Dimension(150, 30));
        buttonParallel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonParallelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(63, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(buttonSequential, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(buttonParallel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(121, 121, 121))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonParallel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSequential, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setText("Select the web sites");

        textAreaPages.setColumns(20);
        textAreaPages.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        textAreaPages.setRows(5);
        jScrollPane1.setViewportView(textAreaPages);

        jButton1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton1.setText("Load pages");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setPreferredSize(new java.awt.Dimension(150, 30));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton3.setText("Clean");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setPreferredSize(new java.awt.Dimension(150, 30));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(147, 147, 147))))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText("Search results");

        textAreaResults.setEditable(false);
        textAreaResults.setContentType("text/html"); // NOI18N
        textAreaResults.setPreferredSize(new java.awt.Dimension(600, 447));
        jScrollPane3.setViewportView(textAreaResults);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(257, 257, 257))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonStatistics.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        buttonStatistics.setText("See statistics");
        buttonStatistics.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonStatistics.setPreferredSize(new java.awt.Dimension(150, 30));
        buttonStatistics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStatisticsActionPerformed(evt);
            }
        });

        buttonSearch.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        buttonSearch.setText("Search");
        buttonSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        buttonSearch.setPreferredSize(new java.awt.Dimension(150, 30));
        buttonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSearchActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setText("Write the word/words to search");

        textSearch.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        textSearch.setPreferredSize(new java.awt.Dimension(360, 30));
        textSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textSearchKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(399, 399, 399)
                .addComponent(buttonSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttonStatistics, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(410, 410, 410))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(textSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(139, 139, 139))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonStatistics, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            String line;
            FileReader file;
            try {
                file = new FileReader(selectedFile);
                BufferedReader b = new BufferedReader(file);
                try {
                    while((line = b.readLine())!=null) {
                        if(textAreaPages.getText().equals(""))
                            textAreaPages.append(line);
                        else
                            textAreaPages.append("\n" + line);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Browser.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    b.close();
                } catch (IOException ex) {
                    Logger.getLogger(Browser.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Browser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        textAreaPages.setText("");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void buttonSequentialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSequentialActionPerformed
        selectSequential();
    }//GEN-LAST:event_buttonSequentialActionPerformed

    private void buttonParallelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonParallelActionPerformed
        selectParallel();
    }//GEN-LAST:event_buttonParallelActionPerformed

    private void buttonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSearchActionPerformed
        try {
            search();
        } catch (IOException ex) {
            Logger.getLogger(Browser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_buttonSearchActionPerformed

    private void buttonStatisticsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStatisticsActionPerformed
        createChartComparison();
        createChartAppearances();
        myStatistics.setVisible(true);
    }//GEN-LAST:event_buttonStatisticsActionPerformed

    private void textSearchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textSearchKeyTyped
        if(evt.getKeyChar() == '\n'){
            try {
                search();
            } catch (IOException ex) {
                Logger.getLogger(Browser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_textSearchKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Browser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Browser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Browser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Browser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Browser().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonParallel;
    private javax.swing.JButton buttonSearch;
    private javax.swing.JButton buttonSequential;
    private javax.swing.JButton buttonStatistics;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    public javax.swing.JTextArea textAreaPages;
    public javax.swing.JTextPane textAreaResults;
    public javax.swing.JTextField textSearch;
    // End of variables declaration//GEN-END:variables
}