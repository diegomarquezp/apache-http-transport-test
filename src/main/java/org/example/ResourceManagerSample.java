package org.example;

import com.google.api.client.googleapis.apache.v2.GoogleApacheHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.cloudresourcemanager.v3.CloudResourceManager;
import com.google.api.services.cloudresourcemanager.v3.CloudResourceManager.Projects;
import com.google.api.services.cloudresourcemanager.v3.CloudResourceManager.Projects.Get;
import com.google.api.services.cloudresourcemanager.v3.model.Project;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class ResourceManagerSample {
    public static void printProject(HttpTransport transport, String label) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        CloudResourceManager.Builder resourceManagerBuilder =
                new CloudResourceManager.Builder(
                        transport, jsonFactory, new HttpCredentialsAdapter(credentials))
                        .setApplicationName("Example Java App");
        CloudResourceManager cloudResourceManager = resourceManagerBuilder.build();

        Projects projects = cloudResourceManager.projects();

        Get get = projects.get("projects/cloud-devrel-public-resources");

        Project project = get.execute();
        System.out.println("**************************************************************");
        System.out.println("Project display name: " + project.getDisplayName());
        System.out.println(label + " successfully obtained the project");
        System.out.println("**************************************************************");
    }
    public static void main(String[] arguments) throws Exception {
        // 1. Default ApacheHttpTransport()
        printProject(new ApacheHttpTransport(), "Default ApacheHttpTransport");

        // 2. ApacheHttpTransport(HttpClient) with default HttpClient
        HttpClient client = ApacheHttpTransport.newDefaultHttpClient();
        printProject(new ApacheHttpTransport(client), "ApacheHttpTransport with default HttpClient");

        // 3. Custom interceptor
        client = ApacheHttpTransport.newDefaultHttpClientBuilder()
                .addInterceptorFirst(new HttpRequestInterceptor() {
                    @Override
                    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                        System.out.println("custom interception");
                    }
                })
                .build();
        printProject(new ApacheHttpTransport(client), "ApacheHttpTransport with custom HttpClient");


        // 4. Default GoogleApacheHttpTransport()
        printProject(GoogleApacheHttpTransport.newTrustedTransport(), "Default GoogleApacheHttpTransport");
    }
}
