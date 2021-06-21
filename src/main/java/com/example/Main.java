package com.example;

import java.sql.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index() {
    return "index";
  }
  
  @RequestMapping("/add")
  String addmvn() {
    return "add";
  }
  @RequestMapping("/about")
  String about(){
    return "about";
  }

  @RequestMapping("/box")
  String box(){
    return "box";
  }

  @RequestMapping("/success")
  String returnSuccess(){
    return "success";
  }
  

  @GetMapping(
    path = "/box"
  )
  public String getBoxForm(Map<String, Object> model){
    Box box = new Box();  // creates new person object with empty fname and lname
    model.put("box", box);
    return "box";
  }

  @PostMapping(
    path = "/box",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleBrowserBoxSubmit(Map<String, Object> model, Box box) throws Exception {
    // Save the person data into the database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS box (id serial, name varchar(20), color varchar(20), width varchar(20), height varchar(20))");
      String sql = "INSERT INTO box (name,color, width, height) VALUES ('" + box.getName() + "','" + box.getColor() + "','" + box.getWidth() + "','" + box.getHeight() + "')";
      stmt.executeUpdate(sql);
      System.out.println(box.getName() + " " + box.getColor() + " " + box.getWidth() + " " + box.getHeight()); // print person on console
      return "redirect:/box/success";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }

  }

  @GetMapping("/box/success")
  public String getBoxSuccess(Map<String, Object> model){
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM box");

      ArrayList<Box> output = new ArrayList<>();
      while (rs.next()) {
        Box temp = new Box();
        String name = rs.getString("name");
        String id = rs.getString("id");
        String color = rs.getString("color");
        String height = rs.getString("height");
        String width = rs.getString("width");
        temp.setName(name);
        temp.setColor(color);
        temp.setWidth(width);
        temp.setHeight(height);
        temp.setId(id);

        
        output.add(temp);
        System.out.print(output.get(output.size()-1).getId());
        System.out.print(" ");
        System.out.print(output.get(output.size()-1).getName());
        System.out.print(" ");
        System.out.print(output.get(output.size()-1).getColor());
        System.out.print(" ");
        System.out.print(output.get(output.size()-1).getWidth());
        System.out.print(" ");
        System.out.print(output.get(output.size()-1).getHeight());
        System.out.println();
      }

      model.put("records", output);
      return "success";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
    
  }

  // Method 1: uses Path variable to specify person
  @GetMapping("/box/read/{pid}")
  public String getSpecificBox(Map<String, Object> model, @PathVariable String pid){
    System.out.println(pid);
    // 
    // query DB : SELECT * FROM box WHERE id={pid}
    model.put("id", pid);

    try (Connection connection = dataSource.getConnection()) {
      System.out.println("try");
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM box where id = " + pid);
      while(rs.next()){
      String name1 = rs.getString("name");
      String id1 = rs.getString("id");
      String color1 = rs.getString("color");
      String height1 = rs.getString("height");
      String width1 = rs.getString("width");
      
      model.put("name1", name1);
      model.put("id1", id1);
      model.put("color1", color1);
      model.put("height1", height1);
      model.put("width1", width1);
      }
    }catch(Exception e){
      model.put("message", e.getMessage());
      System.out.println("catch");
      return "error";
    }
    return "readbox";
  }

  // Method 2: uses query string to specify person
  @GetMapping("/box/read")
  public String getSpecificBox2(Map<String, Object> model, @RequestParam String pid){
    System.out.println(pid);
    // 
    // query DB : SELECT * FROM box WHERE id={pid}
    model.put("id", pid);
    return "readbox";
  }

  @ResponseBody
  @GetMapping("/box/del/{pid}")
  public String DeleteBox(Map<String, Object> model, @PathVariable String pid)throws Exception{
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      System.out.println(pid);
      stmt.executeUpdate("DELETE FROM box WHERE id="+Integer.parseInt(pid));
      System.out.println("execute");
      return "redirect:/box/success";
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return "redirect:/box/success";
  }
}

  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

}
