/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.lucafaggion.thesis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @ComponentScan("com.lucafaggion.thesis.develop")
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  // //
  // // https://mkyong.com/spring-boot/how-to-display-all-beans-loaded-by-spring-boot/#:~:text=In%20Spring%20Boot%2C%20you%20can,loaded%20by%20the%20Spring%20container.
  // @Bean
  // public CommandLineRunner run(ApplicationContext appContext) throws IOException {
  //   return args -> {

  //     FileWriter beanFile = new FileWriter("src/main/resources/beans.txt");
  //     String[] beans = appContext.getBeanDefinitionNames();
  //     for (String bean : beans) {
  //       beanFile.write(bean + " of Type :: " + appContext.getBean(bean).getClass() +
  //           "\n");
  //     }
  //     beanFile.close();
  //   };
  // }

  // @Bean
  // public CommandLineRunner templateEngineTest(SpringTemplateEngine templateEngine) throws IOException {
  //   return args -> {
  //     HashMap<String, Object> user = new HashMap<String, Object>();
  //     user.put("name", "lucafaggion");

  //     HashMap<String, Object> mapContext = new HashMap<String, Object>();
  //     mapContext.put("name", "Testing Name");
  //     mapContext.put("user", user);

  //     Context templateContext = new Context(new Locale("en"), mapContext);

  //     FileWriter templateWriter = new FileWriter("src/main/resources/configs/testconfig_compiled.yaml");
  //     templateEngine.process("testconfig", templateContext, templateWriter);

  //     FileWriter templateWriterText = new FileWriter("src/main/resources/configs/testconfig_compiled_fromtext.yaml");
  //     String template = """
  //         name: GitHub Actions Demo
  //         on: [push]
  //         jobs:
  //         Explore-GitHub-Actions:
  //         steps:
  //         - run: echo \"🎉 The job was automatically triggered by a ${{
  //         github.event_name }} event.\"
  //         - run: echo \"🐧 This job is now running on a ${{ runner.os }} server hosted
  //         by GitHub!\"
  //         - run: echo \"🔎 The name of your branch is ${{ github.ref }} and your
  //         repository is ${{ github.repository }}.\"
  //         - name: Check out repository code
  //         uses: actions/checkout@v3
  //         - run: echo \"💡 The [(${user.name})] repository has been cloned to the
  //         runner.\"
  //         - run: echo \"🖥️ The workflow is now ready to test your code on the
  //         runner.\"
  //         - name: List files in the repository
  //         run: |
  //         ls ${{ github.workspace }}
  //         - run: echo \"🍏 This job's status is ${{ job.status }}.\"
  //         """;
  //     templateEngine.process(template, templateContext, templateWriterText);
  //   };
  // }

  // @Bean
  // public CommandLineRunner templateEngineClasses(RunnerTaskConfigService runnerTaskConfigService,
  //     RunnableGraphService runnableGraphService) throws IOException {
  //   return args -> {

  //     HashMap<String, Object> user = new HashMap<String, Object>();
  //     user.put("name", "lucafaggion");

  //     HashMap<String, Object> mapContext = new HashMap<String, Object>();
  //     mapContext.put("name", "Testing Name");
  //     mapContext.put("user", user);
  //     Context templateContext = new Context(new Locale("en"), mapContext);

  //     File templateConfig = new File("src/main/resources/configs/testconfig.yaml");
  //     FileWriter templateWriterPlain = new FileWriter("src/main/resources/configs/testconfig_compiled_plain.yaml");
  //     String compiledTemplate = runnerTaskConfigService.compileTemplate(templateConfig, templateContext);
  //     templateWriterPlain.write(compiledTemplate);
  //     templateWriterPlain.close();

  //     // Create a graph
  //     Graph<RunnerAction, RunnableGraphEdge> graph = runnableGraphService
  //         .createAcyclicGraphFromConfig(compiledTemplate);
  //     runnableGraphService.saveGraphToImage(graph,
  //         "src/main/resources/runnerjob_graph.png");
  //   };
  // };

  // @Bean
  // public CommandLineRunner taskRun(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
  //   return args -> {
  //     RunnableGraph graph = new RunnableGraph(threadPoolTaskExecutor.getThreadPoolExecutor());
  //     graph.createGraph();
  //     graph.performRunnableTraversal();
  //     // System.out.println(getGreeting());
  //   };
  // }

  // @Bean
  // public CommandLineRunner testDocker(ApplicationContext appContext,
  //     DockerContainerActionsService dockerActionservice)
  //     throws IOException {
  //   return args -> {
  //     dockerActionservice.runActionInContainer(null);
  //   };
  // }
}