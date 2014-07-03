package software.LDhat;
import java.io.IOException;

/**
 *
 * @author yangyiqing
 */
public class Test {
    public static void main(String[] args)throws IOException{
  
//        new KendallStatistics.KendallStatistics("test.pca.txt", "property.txt", "kendalloutput.txt");
//        new EHH.EHH(0, 4504578, "/Users/yangyiqing/NetBeansProjects/bgi/18_10.genotype.txt", "/Users/yangyiqing/NetBeansProjects/bgi/18_10.genotype.fastphase.inp_hapguess_switch.out.txt","/Users/yangyiqing/NetBeansProjects/bgi/error.txt", "/Users/yangyiqing/NetBeansProjects/bgi/EHH.txt");
 
       new LDhatGeno("10_50.txt", 50000);
//        new LDhat.LDhatResultModify("a.1_12.genotype.txt", "res.txt", "Matching.txt");
//    new LDhat.LDhatResult("10_99.txt", "res.txt", "LDhatMatch.txt");

//        new PositiveSelection.RunSnpWindowsPi("./", "bodysize_new.txt", 50000);
//        new PositiveSelection.RunSnpWindowsTheta("./", "bodysize_new.txt", 50000);
//        new PositiveSelection.Tajima("./", "bodysize_new.txt", 50000, true);
//        new PositiveSelection.Tajima("./", "bodysize_new.txt", 50000, 1, 2,"1_genoInfo.txt");
        
//        new Split.SplitByIndividualNumber("a.1_100.genotype",5000,"c");
//        new Split.SplitBySnpNumber("a.1_100.genotype",5000,"c");
//        new Split.SplitPositionBySnpNotFromZero("1_1141.genotype.txt",5000);
//        new Split.SplitPositionBySnpFromZero("a.1_100.genotype.txt",5000,"b");
//        new Split.SplitSlidingNotFromZero("a.1_100.genotype.txt",100,20);
//        new Split.FastSplitSlidingNotFromZero("1_1141.genotype.txt",1000,200);
       
//       new fastPhase.FastPHASEgeno("try.txt");
//        new fastPhase.FastPHASEvcf("try.vcf");
        
//        new Main.FormatChange("try.vcf");
        
//        new phylip.PhylipGeno("a.1_100.genotype.txt");
        
//        new SmartPCA.SmartPCAgeno("18.genotype", "bodysize_new.txt", "snp", "geno", "ind");
//        new SmartPCA.SmartPCAvcf("try.vcf", "snp", "geno", "ind");
        
//        new structure.StructureGeno("try.genotype.txt", "try.subpopulation");
        
//        new admixture.AdmixtureGeno("./");//need plink first

//        new PositiveSelection.CSS("./", "subpop.txt", 1, 2, 50000);
       
//        new chromoPainter.ChromoPainter("./", "donorLabel.txt", "69sample.txt");
    }
}
    
