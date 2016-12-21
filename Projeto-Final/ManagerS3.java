import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

import java.io.File;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.sun.media.jfxmedia.logging.Logger;
import java.util.logging.Level;


public class ManagerS3{
	private static String bucketName     = "angela-sd";
	private static String keyName;        //nome do arquivo.
	private static String uploadFileName;// = "*** Provide file name ***";
	

	public ManagerS3(){}

	public void send(Path caminho){
            //AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
            //this.uploadFileName = caminho.toString();
            this.uploadFileName = caminho.toString();
            String[] parts = uploadFileName.split("\\\\");
            //String[] parts = uploadFileName.split("/");
            int tam = parts.length;
            this.keyName = parts[tam-1];
            //System.out.println(keyName);
            AWSCredentials credentials = null;
            try {
                credentials = new ProfileCredentialsProvider().getCredentials();
            } catch (Exception e) {
                throw new AmazonClientException(
                        "Cannot load the credentials from the credential profiles file. " +
                        "Please make sure that your credentials file is at the correct " +
                        "location (~/.aws/credentials), and is in valid format.",
                        e);
            }
            AmazonS3 s3client = new AmazonS3Client(credentials);
            Region sp = Region.getRegion(Regions.SA_EAST_1);
            s3client.setRegion(sp);
            try{
                System.out.println("Uploading a new object to S3 from a file\n");
                File file = new File(uploadFileName);
                s3client.putObject(new PutObjectRequest(
                                             bucketName, keyName, file));

             } catch (AmazonServiceException ase) {
                System.out.println("Caught an AmazonServiceException, which " +
                            "means your request made it " +
                        "to Amazon S3, but was rejected with an error response" +
                        " for some reason.");
                System.out.println("Error Message:    " + ase.getMessage());
                System.out.println("HTTP Status Code: " + ase.getStatusCode());
                System.out.println("AWS Error Code:   " + ase.getErrorCode());
                System.out.println("Error Type:       " + ase.getErrorType());
                System.out.println("Request ID:       " + ase.getRequestId());
            } catch (AmazonClientException ace) {
                System.out.println("Caught an AmazonClientException, which " +
                            "means the client encountered " +
                        "an internal error while trying to " +
                        "communicate with S3, " +
                        "such as not being able to access the network.");
                System.out.println("Error Message: " + ace.getMessage());
            }
            System.out.println("Foi criado o arquivo: "+caminho);
	}
	
        private static void receive(String caminho) throws IOException{
            //AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
            //this.uploadFileName = caminho.toString();
            //AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
            //this.uploadFileName = caminho.toString();
            uploadFileName = caminho.toString();
            String[] parts = uploadFileName.split("\\\\");
            //String[] parts = uploadFileName.split("/");
            int tam = parts.length;
            keyName = parts[tam-1];
            caminho = "C:\\Users\\Angela\\Documents\\Faculdade\\SD\\trab-SD\\Projeto-Final\\computador1\\"+caminho;
            //System.out.println(keyName);
 
            //keyName = caminho;
            //System.out.println(keyName);
            AWSCredentials credentials = null;
            try {
                credentials = new ProfileCredentialsProvider().getCredentials();
            } catch (Exception e) {
                throw new AmazonClientException(
                        "Cannot load the credentials from the credential profiles file. " +
                        "Please make sure that your credentials file is at the correct " +
                        "location (~/.aws/credentials), and is in valid format.",
                        e);
            }
            AmazonS3 s3client = new AmazonS3Client(credentials);
            Region sp = Region.getRegion(Regions.SA_EAST_1);
            s3client.setRegion(sp);
            try{
                System.out.println("Downloading object: "+ caminho +"\n");
                //File file = new File(caminho+"/"+keyName);
                File file = new File(caminho);
                S3Object s3object = s3client.getObject(new GetObjectRequest(bucketName, keyName));
                save(s3object.getObjectContent(), caminho);
             } catch (AmazonServiceException ase) {
                System.out.println("Caught an AmazonServiceException, which " +
                            "means your request made it " +
                        "to Amazon S3, but was rejected with an error response" +
                        " for some reason.");
                System.out.println("Error Message:    " + ase.getMessage());
                System.out.println("HTTP Status Code: " + ase.getStatusCode());
                System.out.println("AWS Error Code:   " + ase.getErrorCode());
                System.out.println("Error Type:       " + ase.getErrorType());
                System.out.println("Request ID:       " + ase.getRequestId());
            } catch (AmazonClientException ace) {
                System.out.println("Caught an AmazonClientException, which " +
                            "means the client encountered " +
                        "an internal error while trying to " +
                        "communicate with S3, " +
                        "such as not being able to access the network.");
                System.out.println("Error Message: " + ace.getMessage());
            }
            System.out.println("Foi criado o arquivo: "+caminho);
	}

        public void remove(Path caminho){
            //AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
            //this.uploadFileName = caminho.toString();
            uploadFileName = caminho.toString();
            String[] parts = uploadFileName.split("\\\\");
            //String[] parts = uploadFileName.split("/");
            int tam = parts.length;
            keyName = parts[tam-1];
            AWSCredentials credentials = null;
            try {
                credentials = new ProfileCredentialsProvider().getCredentials();
            } catch (Exception e) {
                throw new AmazonClientException(
                        "Cannot load the credentials from the credential profiles file. " +
                        "Please make sure that your credentials file is at the correct " +
                        "location (~/.aws/credentials), and is in valid format.",
                        e);
            }
            AmazonS3 s3client = new AmazonS3Client(credentials);
            Region sp = Region.getRegion(Regions.SA_EAST_1);
            s3client.setRegion(sp);

            s3client.deleteObject(new DeleteObjectRequest(bucketName, keyName));

            System.out.println("Foi deletado o arquivo: "+caminho);
        }

        private static ArrayList<String> getFileNamesS3() {
            ArrayList<String> f = new ArrayList<String>();            
            //Coisas da credencial
            AWSCredentials credentials = null;
            try {
                credentials = new ProfileCredentialsProvider().getCredentials();
            } catch (Exception e) {
                throw new AmazonClientException(
                        "Cannot load the credentials from the credential profiles file. " +
                        "Please make sure that your credentials file is at the correct " +
                        "location (~/.aws/credentials), and is in valid format.",
                        e);
            }
            AmazonS3 s3client = new AmazonS3Client(credentials);
            Region sp = Region.getRegion(Regions.SA_EAST_1);
            s3client.setRegion(sp);
            //Pega os objetos no bucket salva em um dicionário e add no array os itens.
            //Ordena os itens para fazer a comparação mais pra frente.
            //Retorna o array ordenado.
            ListObjectsRequest listObj= new ListObjectsRequest().withBucketName(bucketName);
            ObjectListing objectListing = s3client.listObjects(listObj);
            for (S3ObjectSummary summary: objectListing.getObjectSummaries()) {
                f.add(summary.getKey());
            }

            Collections.sort(f);
            return f;
        } 
        
        private static ArrayList<String> getFilenamesLocal(String caminho, String fileName) {
            ArrayList<String> localFiles = new ArrayList<String>();
            File folder = new File(caminho);

            File[] listFiles = folder.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                localFiles.add(listFiles[i].getName());
            }
            Collections.sort(localFiles);
            return localFiles;
        }

        private static ArrayList<String> getFilesS3(ArrayList<String> s3, ArrayList<String> local) {
           //System.out.println("Entrou aqui na comparacao");
            ArrayList<String> newFiles = new ArrayList<String>();

            boolean diferente, flag = false; //Indica se o arquivo está nos dois locais (cloud + local)
            //boolean got = false;//
            //Arquivos q estiverem no s3 e não estiverem no local devem ser baixados
            for(int i = 0; i < s3.size(); i++) {
                diferente = true;
                //String aux = "C:\\Users\\Angela\\Documents\\Faculdade\\SD\\trab-SD\\Projeto-Final\\computador1";
                for(int j = 0; j < local.size(); j++) {
                    //System.out.println("teste::"+aux+local.get(j));
                    //if(s3.get(i).equals(aux+local.get(j))) {
                    if(s3.get(i).equals(local.get(j))) {
                        //System.out.println("EEEEEEITA EH IGUAL");
                        diferente = false;
                        flag = true;
                    }
                }
                if(diferente) newFiles.add(s3.get(i));
            }

            if(flag){
                Collections.sort(newFiles);
                return newFiles;    
            }else return null;
        }
        
        private static void downloadFilesS3(ArrayList<String> toDownload) throws IOException {
            for(int i = 0; i < toDownload.size(); i++) {
                if(toDownload != null)
                    receive(toDownload.get(i));
            }
        }
        
        private static void save(S3ObjectInputStream input, String nameFile) throws IOException{
            OutputStream outStream = null;
            outStream = new FileOutputStream(new File(nameFile));

            int read = 0;
            byte[] bytes = new byte[10250000];

            while ((read = input.read(bytes)) != -1) {
                outStream.write(bytes, 0, read);
            }

            System.out.println(nameFile + "' saved.");

        }
        /* Verifica se houve alteração na cloud a cada 5seg*/
        public static void verifyS3(String caminho) {        
            
            (new Thread() {
                @Override
                public void run() {
                    while(true) {   
                       uploadFileName = caminho;
                        String[] parts = uploadFileName.split("\\\\");
                        //String[] parts = uploadFileName.split("/");
                        int tam = parts.length;
                        keyName = parts[tam-1];
                        
                        ArrayList<String> cloudChanges = getFileNamesS3();
                        ArrayList<String> localFiles = getFilenamesLocal(caminho, keyName);

                        try {
                            ArrayList<String> filesToDownload = getFilesS3(cloudChanges, localFiles);
                            if(filesToDownload != null) {
                                downloadFilesS3(filesToDownload);
                                filesToDownload.clear();
                                //downloadedFromCloud = true;
                                Thread.sleep(500);
                            }

                            Thread.sleep(2000);
                        
                        } catch (IOException ex) {
                            java.util.logging.Logger.getLogger(ManagerS3.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            java.util.logging.Logger.getLogger(ManagerS3.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }                
               }
            }).start();
        }
        

}
