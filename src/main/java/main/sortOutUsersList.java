package main;

import api.Ascm_Api;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.AscmUserInfo;
import entity.ecsInfo;
import entity.requestParams;
import util.FileUtils;
import util.saveAsFileWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class sortOutUsersList {
    public static requestParams rp = new requestParams();
    public static Ascm_Api aa = new Ascm_Api();
    public static saveAsFileWriter safw = new saveAsFileWriter();
    public static Scanner scan = new Scanner(System.in);
    public static String displayName;
    public static String choose = "0";
    public static List<AscmUserInfo> auilist = new ArrayList<>();
    public static String orgTemp;
    public static String loginNameTemp;
    public static String displayNameTemp;
    public static StringBuilder roleListTemp = new StringBuilder();
    public static String statusTemp;




    public static void main(String[] args) throws IOException, InterruptedException {
        while(true) {
            cls();
            System.out.println(".____    .__                      \r\n"
                    + "|    |   |__| ____ ________ ____  \r\n"
                    + "|    |   |  |/    \\\\___   //    \\ \r\n"
                    + "|    |___|  |   |  \\/    /|   |  \\\r\n"
                    + "|_______ \\__|___|  /_____ \\___|  /\r\n"
                    + "        \\/       \\/      \\/    \\/ \n");
            System.out.println("---------------------ASCM用户列表导出--------------------");
            System.out.println("\n	请将配置文件conf.txt放入jar包所在的目录\n");
            System.out.println("         [conf.txt                  	]");
            System.out.println("+ -- -- =[域名                       	]");
            System.out.println("+ -- -- =[API网关                    	]");
            System.out.println("+ -- -- =[AccessKeyId                	]");
            System.out.println("+ -- -- =[AccessKeySecret            	]");
            System.out.println("\n	请输入选项：\n");
            System.out.println("+ -- -- =(1、文件已放入相应目录,认证信息	)");
            System.out.println("+ -- -- =(2、退出				)\n");
            System.out.println("---------------------------------------------------------");

            choose = scan.next();
            switch (choose) {
                case "1":
                    //checkLogin() == true
                    if(checkLogin()) {
                        while(true) {
                            cls();
                            System.out.println("---------------------ASCM用户列表导出---------------------");
                            System.out.println("\n+ -- -- =>>用户:	" + displayName + "		<<\n");
                            System.out.println("\n---------------------------------------------------------\n");
                            System.out.println("\n	请选择：\n");
                            System.out.println("+ -- -- =(1、执行		)\n");
                            System.out.println("+ -- -- =(2、退出		)");
                            System.out.println("----------------------------------------------------");

                            choose = scan.next();
                            switch (choose) {
                                case "1":
                                    execute();
                                    return ;
                                case "2":

                                    return ;
                                default:
                                    cls();
                                    System.out.println("输入有误，请重输！");
                                    break;
                            }
                        }
                    }
                    cls();
                    System.out.println("读取文件失败，请重试！");
                    break;
                case "2":
                    return ;
                default:
                    cls();
                    System.out.println("输入有误，请重输！");
                    break;
            }
        }
    }


    public static void execute() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            String resultJson = aa.ListUsers(rp);
//            resultJson.replace("\"default\"","\"Default\"");
            //"totalPage": 1    不存在分页
            if(!resultJson.contains("\"totalPage\":1")){
                System.out.println("存在分页,数据不完整");
                return ;
            }

            JsonNode Usersjn = mapper.readTree(resultJson);
            Usersjn = Usersjn.get("data");
            for(int i=0;i<Usersjn.size();i++){
                AscmUserInfo aui = new AscmUserInfo();
                aui = (AscmUserInfo) mapper.readValue(Usersjn.get(i).toString(), AscmUserInfo.class);
                auilist.add(i,aui);
            }
            System.out.println(auilist.size());

            StringBuilder sbur = new StringBuilder();
            sbur.append("组织,登录名,显示名,角色,状态\n");

            for(int i=0;i<auilist.size();i++){
                orgTemp = auilist.get(i).getOrganization().getName();
                loginNameTemp = auilist.get(i).getLoginName();
                displayNameTemp = auilist.get(i).getDisplayName();
                roleListTemp = new StringBuilder();
                if(auilist.get(i).getRoles() == null){
                    roleListTemp.append("空");
                } else {
                    for(int j=0;j<auilist.get(i).getRoles().size();j++){
                        if(j != 0)
                            roleListTemp.append("||");

                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("defaultRole.resourceUser.name")){
                            roleListTemp.append("资源使用人");
                            continue;
                        }
                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("defaultRole.resourceSetAdmin.name")){
                            roleListTemp.append("资源集管理员");
                            continue;
                        }
                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("defaultRole.yundunGlobalOrgAdmin.name")){
                            roleListTemp.append("全局组织安全管理员");
                            continue;
                        }
                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("defaultRole.yundunGlobalOrgSecAuditor.name")){
                            roleListTemp.append("全局组织安全监察员");
                            continue;
                        }
                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("defaultRole.organizationAdmin.name")){
                            roleListTemp.append("组织管理员");
                            continue;
                        }
                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("defaultRole.yundunOrgSecAdmin.name")){
                            roleListTemp.append("组织安全管理员");
                            continue;
                        }
                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("defaultRole.yundunPlatSecAuditor.name")){
                            roleListTemp.append("平台安全监察员");
                            continue;
                        }
                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("defaultRole.yundunSecAuditor.name")){
                            roleListTemp.append("安全审计员");
                            continue;
                        }
                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("defaultRole.yundunPlatSecAdmin.name")){
                            roleListTemp.append("平台安全管理员");
                            continue;
                        }
                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("defaultRole.globalResourceAuditor.name")){
                            roleListTemp.append("全局资源监察员");
                            continue;
                        }
                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("defaultRole.yundunSecSysConfAdmin.name")){
                            roleListTemp.append("安全系统配置管理员");
                            continue;
                        }
                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("defaultRole.yundunPlatSecConfAdmin.name")){
                            roleListTemp.append("平台安全配置管理员");
                            continue;
                        }
                        if(auilist.get(i).getRoles().get(j).getRoleName().equals("ISV")){
                            roleListTemp.append("ISV");
                            continue;
                        }
                        roleListTemp.append(auilist.get(i).getRoles().get(j).getRoleName());
                    }
                }

                if(auilist.get(i).getStatus().equals("ACTIVE"))
                    statusTemp = "已激活";
                if(auilist.get(i).getStatus().equals("INACTIVE"))
                    statusTemp = "已禁用";

                sbur.append(orgTemp + "," + loginNameTemp + "," + displayNameTemp + "," + roleListTemp.toString() + "," + statusTemp);
                sbur.append("\n");
            }


            //全局组织安全管理员,全局组织安全监察员,组织管理员,组织安全管理员,平台安全监察员,安全审计员,平台安全管理员,全局资源监察员,安全系统配置管理员
            //defaultRole.yundunGlobalOrgAdmin.name
            // ||defaultRole.yundunGlobalOrgSecAuditor.name
            // ||defaultRole.organizationAdmin.name
            // ||defaultRole.yundunOrgSecAdmin.name
            // ||defaultRole.yundunPlatSecAuditor.name
            // ||defaultRole.yundunSecAuditor.name
            // ||defaultRole.yundunPlatSecAdmin.name
            // ||defaultRole.globalResourceAuditor.name
            // ||defaultRole.yundunSecSysConfAdmin.name



            //保存
            String jarpath = System.getProperty("java.class.path");
            int firstIndex = jarpath.lastIndexOf(System.getProperty("path.separator")) + 1;
            int lastIndex = jarpath.lastIndexOf(File.separator) + 1;
            jarpath = jarpath.substring(firstIndex, lastIndex);


            safw.saveAsFileWriter(sbur.toString(), jarpath + "ASCM_users.csv");

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static boolean checkLogin() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            String jarpath = System.getProperty("java.class.path");
            int firstIndex = jarpath.lastIndexOf(System.getProperty("path.separator")) + 1;
            int lastIndex = jarpath.lastIndexOf(File.separator) + 1;
            jarpath = jarpath.substring(firstIndex, lastIndex);

            File conffile =new File(jarpath + "conf.txt");
            if(conffile.exists() != true) {
                System.out.println("conf.txt文件不存在！");
                return false;
            }
            InputStreamReader confrd = new InputStreamReader (new FileInputStream(conffile),"UTF-8");
            BufferedReader confbf = new BufferedReader(confrd);

            rp.setRegionId(confbf.readLine());
            rp.setApiGateWay(confbf.readLine());
            rp.setAccessKeyId(confbf.readLine());
            rp.setAccessKeySecret(confbf.readLine());


            String userinfojson = aa.GetUserInfo(rp);
            JsonNode userinfojn = mapper.readTree(userinfojson);
            userinfojn = userinfojn.get("data").get("displayName");
            displayName = userinfojn.toString();
            rp.setDisplayName(displayName);

            return true;
        } catch (Exception e) {
            System.out.println("读取文件出错！");
            e.printStackTrace();
            return false;
        }
    }

    public static void cls() throws IOException, InterruptedException{
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }
}
