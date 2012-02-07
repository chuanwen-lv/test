package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.apache.jasper.runtime.*;
import org.archive.crawler.admin.CrawlJobHandler;
import org.archive.crawler.admin.CrawlJob;
import org.archive.crawler.Heritrix;
import org.archive.crawler.framework.CrawlController;
import java.util.ListIterator;
import java.util.regex.Pattern;
import org.archive.crawler.settings.CrawlerSettings;
import org.archive.crawler.admin.ui.JobConfigureUtils;
import org.archive.crawler.settings.XMLSettingsHandler;
import org.archive.crawler.settings.refinements.Refinement;
import java.text.SimpleDateFormat;
import org.archive.crawler.Heritrix;
import org.archive.crawler.admin.CrawlJob;
import org.archive.util.ArchiveUtils;
import org.archive.util.TextUtils;
import org.archive.crawler.admin.StatisticsTracker;
import org.archive.crawler.Heritrix;
import java.util.Map;
import java.util.Iterator;

public class overview_jsp extends HttpJspBase {


  private static java.util.Vector _jspx_includes;

  static {
    _jspx_includes = new java.util.Vector(6);
    _jspx_includes.add("/include/handler.jsp");
    _jspx_includes.add("/include/head.jsp");
    _jspx_includes.add("/include/stats.jsp");
    _jspx_includes.add("/include/jobnav.jsp");
    _jspx_includes.add("/include/jobpernav.jsp");
    _jspx_includes.add("/include/foot.jsp");
  }

  public java.util.List getIncludes() {
    return _jspx_includes;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    JspFactory _jspxFactory = null;
    javax.servlet.jsp.PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;


    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			"/error.jsp", true, 8192, true);
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;


  /**
   * This pages displays existing refinements on a particular override (or global settings).
   * Allows user to delete them and creat new ones.
   *
   * @author Kristinn Sigurdsson
   */

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");

    /**
     * This include page ensures that the handler exists and is ready to be
     * accessed.
     */
    CrawlJobHandler handler =
        (CrawlJobHandler)application.getAttribute("handler");
    Heritrix heritrix = (Heritrix)application.getAttribute("heritrix");
    
    // If handler is empty then this is the first time this bit of code is
    // being run since the server came online. In that case get or create the
    // handler.
    if (handler == null) {
        if(Heritrix.isSingleInstance()) {
            heritrix = Heritrix.getSingleInstance();
            handler = heritrix.getJobHandler();
            application.setAttribute("heritrix", heritrix);
            application.setAttribute("handler", handler);
        } else {
            // TODO:
            // If we get here, then there are multiple heritrix instances
            // and we have to put up a screen allowing the user choose between.
            // Otherwise, there is no Heritrix instance.  Thats a problem.
            throw new RuntimeException("No heritrix instance (or multiple " +
                    "to choose from and we haven't implemented this yet)");
        }
    }
    
    // ensure controller's settingsHandler is always thread-installed 
    // in web ui threads
    if(handler != null) {
        CrawlJob job = handler.getCurrentJob();
        if(job != null) {
            CrawlController controller = job.getController();
            if (controller != null) {
                controller.installThreadContextSettingsHandler();
            }
        }
    }

      out.write("\n");
      out.write("\n\n");
      out.write("\n");
      out.write("\n\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n\n");

    // Load display level
    String currDomain = request.getParameter("currDomain");
    // Load the job to manipulate   
    CrawlJob theJob = JobConfigureUtils.checkCrawlJob(
        handler.getJob(request.getParameter("job")), response,
        request.getContextPath() + "/jobs.jsp", currDomain);
    if (theJob == null) {
        return;
    }

    String message = request.getParameter("message");

    // Get the settings objects.
    XMLSettingsHandler settingsHandler = theJob.getSettingsHandler();
    boolean global = currDomain == null || currDomain.length() == 0;


    CrawlerSettings localSettings;
    
    if(global){
        localSettings = settingsHandler.getSettingsObject(null);
    } else {
        localSettings = settingsHandler.getSettingsObject(currDomain);
    }
    
    // Check for actions
    String action = request.getParameter("action");
    if(action != null){
        // Need to do something!
        if(action.equals("done")){
            // Ok, done editing.
            if(global){
                if(theJob.isNew()){         
                    handler.addJob(theJob);
                    response.sendRedirect(request.getContextPath () +
                        "/jobs.jsp?message=Job created");
                }else{
                    if(theJob.isRunning()){
                        handler.kickUpdate();
                    }
                    if(theJob.isProfile()){
                        response.sendRedirect(request.getContextPath () +
                            "/profiles.jsp?message=Profile modified");
                    }else{
                        response.sendRedirect(request.getContextPath () +
                           "/jobs.jsp?message=Job modified");
                    }
                }
            } else {
                if(theJob.isRunning()){
                    handler.kickUpdate();
                }
                response.sendRedirect(request.getContextPath () +
                    "/jobs/per/overview.jsp?job=" + theJob.getUID() +
                    "&currDomain=" + currDomain +
                    "&message=Override changes saved");
            }
            return;
        } else if(action.equals("new")){
            // Add new refinement
            String reference = request.getParameter("newReference");
            String description = request.getParameter("newDescription");
            // Ensure we got a valid name. ([a-zA-Z][0-9][-_])
            Pattern p = Pattern.compile("[a-zA-Z_\\-0-9]*");
            if(p.matcher(reference).matches()==false){
                // Illegal name!
                message = "Name can only contain alphanumeric chars, dash and underscore.<br>No spaces are allowed";
            } else {
                // Got a valid name create the sucker
                Refinement newRef = new Refinement(localSettings, reference, description);
                settingsHandler.writeSettingsObject(localSettings);
            }
        } else if(action.equals("edit")){
            // Edit an existing refinement.
            response.sendRedirect(request.getContextPath () +
                "/jobs/refinements/criteria.jsp?job=" + theJob.getUID() +
                "&currDomain=" + currDomain + "&reference=" +
                request.getParameter("refinement"));
            return;
        } else if(action.equals("delete")){
            // Delete refinement.
            String reference = request.getParameter("refinement");
            localSettings.removeRefinement(reference);
            settingsHandler.writeSettingsObject(localSettings);
        } else if(action.equals("goto")){
            // Goto another page of the job/profile settings
            if(global){
                response.sendRedirect(request.getParameter("where"));
            } else {
                response.sendRedirect(request.getParameter("where")+"&currDomain="+currDomain);
            }
            return;
        }
    }
    
    String title = "Refinements";
    int tab = theJob.isProfile()?2:1;
    int jobtab = 5;

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");

    String currentHeritrixName = (heritrix == null)?
        "No current Heritrix instance":
        (heritrix.getMBeanName() == null)?
            heritrix.getInstances().keySet().iterator().next().toString():
            heritrix.getMBeanName().toString();

    /**
     * An include file that handles the "look" and navigation of a web page. 
     * Include at top (where you would normally begin the HTML code).
     * If used, the include "foot.jsp" should be included at the end of the HTML
     * code. It will close any table, body and html tags left open in this one.
     * Any custom HTML code is thus placed between the two.
     *
     * The following variables must exist prior to this file being included:
     *
     * String title - Title of the web page
     * int tab - Which to display as 'selected'.
     *           0 - Console
     *           1 - Jobs
     *           2 - Profiles
     *           3 - Logs
     *           4 - Reports
     *           5 - Settings
     *           6 - Help
     *
     * SimpleHandler handler - In general this is provided by the include
     *                         page 'handler.jsp' which should be included
     *                         prior to this one.
     *
     * @author Kristinn Sigurdsson
     */
    String shortJobStatus = null;
	if(handler.getCurrentJob() != null) {
		shortJobStatus = TextUtils.getFirstWord(handler.getCurrentJob().getStatus());
	}
	String favicon = System.getProperties().getProperty("heritrix.favicon","h.ico");
	

      out.write("\n");

    StatisticsTracker stats = null;
    if(handler.getCurrentJob() != null) {
        // Assume that StatisticsTracker is being used.
        stats = (StatisticsTracker)handler.getCurrentJob().
            getStatisticsTracking();
    }

      out.write("\n");
      out.write("\n\n");
      out.write("<html>\n    ");
      out.write("<head>\n    \t");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n        ");
      out.write("<title>Heritrix: ");
      out.print(title);
      out.write("</title>\n        ");
      out.write("<link rel=\"stylesheet\" \n            href=\"");
      out.print(request.getContextPath());
      out.write("/css/heritrix.css\">\n        ");
      out.write("<link rel=\"icon\" href=\"");
      out.print(request.getContextPath());
      out.write("/images/");
      out.print(favicon);
      out.write("\" type=\"image/x-icon\" />\n        ");
      out.write("<link rel=\"shortcut icon\" href=\"");
      out.print(request.getContextPath());
      out.write("/images/");
      out.print(favicon);
      out.write("\" type=\"image/x-icon\" />\n        ");
      out.write("<script src=\"/js/util.js\">\n        ");
      out.write("</script>\n    ");
      out.write("</head>\n\n    ");
      out.write("<body>\n        ");
      out.write("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n            ");
      out.write("<tr>\n                ");
      out.write("<td>\n                    ");
      out.write("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" height=\"100%\">\n                        ");
      out.write("<tr>\n                            ");
      out.write("<td height=\"60\" width=\"155\" valign=\"top\" nowrap>\n                                ");
      out.write("<table border=\"0\" width=\"155\" cellspacing=\"0\" cellpadding=\"0\" height=\"60\">\n                                    ");
      out.write("<tr>\n                                        ");
      out.write("<td align=\"center\" height=\"40\" valign=\"bottom\">\n                                            ");
      out.write("<a border=\"0\" \n                                            href=\"");
      out.print(request.getContextPath());
      out.write("/index.jsp\">");
      out.write("<img border=\"0\" src=\"");
      out.print(request.getContextPath());
      out.write("/images/logo.gif\" height=\"37\" width=\"145\">");
      out.write("</a>\n                                        ");
      out.write("</td>\n                                    ");
      out.write("</tr>\n                                    ");
      out.write("<tr>\n                                        ");
      out.write("<td class=\"subheading\">\n                                            ");
      out.print(title);
      out.write("\n                                        ");
      out.write("</td>\n                                    ");
      out.write("</tr>\n                                ");
      out.write("</table>\n                            ");
      out.write("</td>\n                            ");
      out.write("<td width=\"5\" nowrap>\n                                &nbsp;&nbsp;\n                            ");
      out.write("</td>\n                            ");
      out.write("<td width=\"460\" align=\"left\" nowrap>\n                                ");
      out.write("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" height=\"60\">\n                                    ");
      out.write("<tr>\n                                        ");
      out.write("<td colspan=\"2\" nowrap>\n                                            ");

                                                SimpleDateFormat sdf = new SimpleDateFormat("MMM. d, yyyy HH:mm:ss");
                                                sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
                                            
      out.write("\n                                            ");
      out.write("<b>\n                                                Status as of ");
      out.write("<a style=\"color: #000000\" href=\"");
      out.print(request.getRequestURL());
      out.write("\">");
      out.print(sdf.format(new java.util.Date()));
      out.write(" GMT");
      out.write("</a>\n                                            ");
      out.write("</b>\n                                            &nbsp;&nbsp;\n                                            ");
      out.write("<span style=\"text-align:right\">\n                                            ");
      out.write("<b>\n                                                Alerts: \n                                            ");
      out.write("</b>\n                                            ");
 if(heritrix.getAlertsCount() == 0) { 
      out.write("\n                                                ");
      out.write("<a style=\"color: #000000; text-decoration: none\" href=\"");
      out.print(request.getContextPath());
      out.write("/console/alerts.jsp\">no alerts");
      out.write("</a>\n                                            ");
 } else if(heritrix.getNewAlertsCount()>0){ 
      out.write("\n                                                ");
      out.write("<b>");
      out.write("<a href=\"");
      out.print(request.getContextPath());
      out.write("/console/alerts.jsp\">");
      out.print(heritrix.getAlerts().size());
      out.write(" (");
      out.print(heritrix.getNewAlertsCount());
      out.write(" new)");
      out.write("</a>");
      out.write("</b>\n                                            ");
 } else { 
      out.write("\n                                                ");
      out.write("<a style=\"color: #000000\" href=\"");
      out.print(request.getContextPath());
      out.write("/console/alerts.jsp\">");
      out.print(heritrix.getAlertsCount());
      out.write(" (");
      out.print(heritrix.getNewAlertsCount());
      out.write(" new)");
      out.write("</a>\n                                            ");
 } 
      out.write("\n                                            ");
      out.write("</span>\n                                        ");
      out.write("</td>\n                                    ");
      out.write("</tr>\n                                    ");
      out.write("<tr>\n                                        ");
      out.write("<td valign=\"top\" nowrap>\n\t\t\t\t\t\t\t\t\t\t");
      out.print( handler.isRunning()
										    ? "<span class='status'>Crawling Jobs</span>"
										    : "<span class='status'>Holding Jobs</span>"
										);
      out.write("<i>&nbsp;");
      out.write("</i>\n\t\t\t\t\t\t\t\t\t\t");
      out.write("</td>\n\t\t\t\t\t\t\t\t\t\t");
      out.write("<td valign=\"top\" align=\"right\" nowrap>\n\t\t\t\t\t\t\t\t\t\t");

										if(handler.isRunning() || handler.isCrawling()) {
										    if(handler.getCurrentJob() != null)
										    {
      out.write("\n\t\t\t\t\t\t\t\t\t\t");
      out.write("<span class='status'>\n\t\t\t\t\t\t\t\t\t\t");
      out.print( shortJobStatus );
      out.write("</span> job:\n\t\t\t\t\t\t\t\t\t\t");
      out.write("<i>");
      out.print( handler.getCurrentJob().getJobName() );
      out.write("</i>\n\t\t\t\t\t\t\t\t\t\t");

										    } else {
										        out.println("No job ready <a href=\"");
										        out.println(request.getContextPath());
										        out.println("/jobs.jsp\" style='color: #000000'>(create new)</a>");
										     }
										 }
										
      out.write("\n\t\t\t\t\t\t\t\t\t\t");
      out.write("</td>\n                                    ");
      out.write("</tr>\n                                    ");
      out.write("<tr>\n                                        ");
      out.write("<td nowrap>\n                                            ");
      out.print(handler.getPendingJobs().size());
      out.write("\n                                            jobs\n                                            ");
      out.write("<a style=\"color: #000000\" href=\"");
      out.print(request.getContextPath());
      out.write("/jobs.jsp#pending\">pending");
      out.write("</a>,\n                                            ");
      out.print(handler.getCompletedJobs().size());
      out.write("\n                                            ");
      out.write("<a style=\"color: #000000\" href=\"");
      out.print(request.getContextPath());
      out.write("/jobs.jsp#completed\">completed");
      out.write("</a>\n                                            &nbsp;\n                                        ");
      out.write("</td>\n                                        ");
      out.write("<td nowrap align=\"right\">\n                                            ");
 if(handler.isCrawling()){ 
      out.write("\n                                                    ");
      out.print((stats != null)? stats.successfullyFetchedCount(): 0);
      out.write(" URIs in \n\t\t                                            ");
      out.print( ArchiveUtils.formatMillisecondsToConventional( 
		                                            		((stats != null) 
		                                            		  	? (stats.getCrawlerTotalElapsedTime())
		                                            		  	: 0),
		                                            		false
		                                            	)
		                                            );
      out.write("\n\t\t                                            (");
      out.print(ArchiveUtils.doubleToString(((stats != null)? stats.currentProcessedDocsPerSec(): 0),2));
      out.write("/sec)\n                                            ");
 } 
      out.write("\n                                        ");
      out.write("</td>\n                                    ");
      out.write("</tr>\n                                ");
      out.write("</table>\n                            ");
      out.write("</td>\n                        ");
      out.write("</tr>\n                    ");
      out.write("</table>\n                ");
      out.write("</td>\n                ");
      out.write("<td width=\"100%\" nowrap>\n                    &nbsp;\n                ");
      out.write("</td>\n            ");
      out.write("</tr>\n            ");
      out.write("<tr>\n                ");
      out.write("<td bgcolor=\"#0000FF\" height=\"1\" colspan=\"4\">\n                ");
      out.write("</td>\n            ");
      out.write("</tr>\n            ");
      out.write("<tr>\n                ");
      out.write("<td colspan=\"4\" height=\"20\">\n                    ");
      out.write("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" height=\"20\">\n                        ");
      out.write("<tr>\n                            ");
      out.write("<td class=\"tab_seperator\">&nbsp;");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab");
      out.print(tab==0?"_selected":"");
      out.write("\">\n                                ");
      out.write("<a href=\"");
      out.print(request.getContextPath());
      out.write("/index.jsp\" class=\"tab_text");
      out.print(tab==0?"_selected":"");
      out.write("\">Console");
      out.write("</a>\n                            ");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab_seperator\">&nbsp;");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab");
      out.print(tab==1?"_selected":"");
      out.write("\">\n                                ");
      out.write("<a href=\"");
      out.print(request.getContextPath());
      out.write("/jobs.jsp\" class=\"tab_text");
      out.print(tab==1?"_selected":"");
      out.write("\">Jobs");
      out.write("</a>\n                            ");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab_seperator\">&nbsp;");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab");
      out.print(tab==2?"_selected":"");
      out.write("\">\n                                ");
      out.write("<a href=\"");
      out.print(request.getContextPath());
      out.write("/profiles.jsp\" class=\"tab_text");
      out.print(tab==2?"_selected":"");
      out.write("\">Profiles");
      out.write("</a>\n                            ");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab_seperator\">&nbsp;");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab");
      out.print(tab==3?"_selected":"");
      out.write("\">\n                                ");
      out.write("<a href=\"");
      out.print(request.getContextPath());
      out.write("/logs.jsp\" class=\"tab_text");
      out.print(tab==3?"_selected":"");
      out.write("\">Logs");
      out.write("</a>\n                            ");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab_seperator\">&nbsp;");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab");
      out.print(tab==4?"_selected":"");
      out.write("\">\n                                ");
      out.write("<a href=\"");
      out.print(request.getContextPath());
      out.write("/reports.jsp\" class=\"tab_text");
      out.print(tab==4?"_selected":"");
      out.write("\">Reports");
      out.write("</a>\n                            ");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab_seperator\">&nbsp;");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab");
      out.print(tab==5?"_selected":"");
      out.write("\">\n                                ");
      out.write("<a href=\"");
      out.print(request.getContextPath());
      out.write("/setup.jsp\" class=\"tab_text");
      out.print(tab==5?"_selected":"");
      out.write("\">Setup");
      out.write("</a>\n                            ");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab_seperator\">&nbsp;");
      out.write("</td>\n                            ");
      out.write("<td class=\"tab");
      out.print(tab==6?"_selected":"");
      out.write("\">\n                                ");
      out.write("<a href=\"");
      out.print(request.getContextPath());
      out.write("/help.jsp\" class=\"tab_text");
      out.print(tab==6?"_selected":"");
      out.write("\">Help");
      out.write("</a>\n                             ");
      out.write("</td>\n                            ");
      out.write("<td width=\"100%\">\n                            ");
      out.write("</td>\n                        ");
      out.write("</tr>\n                    ");
      out.write("</table>\n                ");
      out.write("</td>\n            ");
      out.write("</tr>\n            ");
      out.write("<tr>\n                ");
      out.write("<td bgcolor=\"#0000FF\" height=\"1\" colspan=\"4\">");
      out.write("</td>\n            ");
      out.write("</tr>\n         ");
      out.write("</table>\n                    ");
      out.write("<!-- MAIN BODY -->\n");
      out.write("\n    ");
      out.write("<script type=\"text/javascript\">\n        function doSubmit(){\n            document.frmRefinements.submit();\n        }\n        \n        function doGoto(where){\n            document.frmRefinements.action.value=\"goto\";\n            document.frmRefinements.where.value = where;\n            doSubmit();\n        }\n        \n        function doEdit(refinement){\n            document.frmRefinements.action.value=\"edit\";\n            document.frmRefinements.refinement.value = refinement;\n            doSubmit();\n        }\n        \n        function doDelete(refinement){\n            document.frmRefinements.action.value=\"delete\";\n            document.frmRefinements.refinement.value = refinement;\n            doSubmit();\n        }\n        \n        function newRefinement(){\n            document.frmRefinements.action.value=\"new\";\n            doSubmit();\n        }\n    ");
      out.write("</script>   \n    ");
 if(message != null && message.length() > 0){ 
      out.write("\n        ");
      out.write("<p>\n            ");
      out.write("<span class=\"flashMessage\">");
      out.write("<b>");
      out.print(message);
      out.write("</b>");
      out.write("</span>\n    ");
 } 
      out.write("\n    ");
      out.write("<p>\n        ");
 if (global) { 
      out.write("\n            ");

    /**
     * An include file that handles the sub navigation of a 'job' page. 
     * Include where the sub navigation should be displayed.
     *
     * The following variables must exist prior to this file being included:
     *
     * String theJob - The CrawlJob being manipulated.
     * int jobtab - Which to display as 'selected'.
     *          0 - Modules
     *          SUPERCEDED BY SUBMODULES 1 - Filters
     *          2 - Settings
     *          3 - Overrides
     *          SUPERCEDED BY SUBMODULES 4 - Credentials
     *          5 - Refinements
     *          SUPERCEDED BY SUBMODULES 6 - URL (Canonicalization)
     *          7 - Submodules 
     *
     * @author Kristinn Sigurdsson
     */

      out.write("\n    ");
      out.write("<table cellspacing=\"0\" cellpadding=\"0\">\n        ");
      out.write("<tr>\n            ");
      out.write("<td bgcolor=\"#0000FF\" height=\"1\">\n            ");
      out.write("</td>\n        ");
      out.write("</tr>\n        ");
      out.write("<tr>\n            ");
      out.write("<td>\n                ");
      out.write("<table cellspacing=\"0\" cellpadding=\"0\">\n                    ");
      out.write("<tr>\n                        ");
      out.write("<td nowrap>\n                            ");
      out.write("<b>");
      out.print(theJob.isProfile()?"Profile":"Job");
      out.write(" ");
      out.print(theJob.getJobName());
      out.write(":");
      out.write("</b>\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab_seperator\">\n                        ");
      out.write("</td>\n                        ");
 if(theJob.isRunning()){ 
      out.write("\n                            ");
      out.write("<td class=\"tab_inactive\" nowrap>\n                                ");
      out.write("<a href=\"javascript:alert('Can not edit modules on running jobs!')\" class=\"tab_text_inactive\">Modules");
      out.write("</a>\n                            ");
      out.write("</td>\n                        ");
 } else { 
      out.write("\n                            ");
      out.write("<td class=\"tab");
      out.print(jobtab==0?"_selected":"");
      out.write("\" nowrap>\n                                ");
      out.write("<a href=\"javascript:doGoto('");
      out.print(request.getContextPath());
      out.write("/jobs/modules.jsp?job=");
      out.print(theJob.getUID());
      out.write("')\" class=\"tab_text");
      out.print(jobtab==0?"_selected":"");
      out.write("\">Modules");
      out.write("</a>\n                            ");
      out.write("</td>\n                        ");
 } 
      out.write("\n                        ");
      out.write("<td class=\"tab_seperator\">\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab");
      out.print(jobtab==7?"_selected":"");
      out.write("\" nowrap>\n                            ");
      out.write("<a href=\"javascript:doGoto('");
      out.print(request.getContextPath());
      out.write("/jobs/submodules.jsp?job=");
      out.print(theJob.getUID());
      out.write("')\" class=\"tab_text");
      out.print(jobtab==7?"_selected":"");
      out.write("\">Submodules");
      out.write("</a>\n                        ");
      out.write("<td class=\"tab_seperator\">\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab");
      out.print(jobtab==2?"_selected":"");
      out.write("\" nowrap>\n                            ");
      out.write("<a href=\"javascript:doGoto('");
      out.print(request.getContextPath());
      out.write("/jobs/configure.jsp?job=");
      out.print(theJob.getUID());
      out.write("')\" class=\"tab_text");
      out.print(jobtab==2?"_selected":"");
      out.write("\">Settings");
      out.write("</a>\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab_seperator\">\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab");
      out.print(jobtab==3?"_selected":"");
      out.write("\" nowrap>\n                            ");
      out.write("<a href=\"javascript:doGoto('");
      out.print(request.getContextPath());
      out.write("/jobs/per/overview.jsp?job=");
      out.print(theJob.getUID());
      out.write("')\" class=\"tab_text");
      out.print(jobtab==3?"_selected":"");
      out.write("\">Overrides");
      out.write("</a>\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab_seperator\">\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab");
      out.print(jobtab==5?"_selected":"");
      out.write("\" nowrap>\n                            ");
      out.write("<a href=\"javascript:doGoto('");
      out.print(request.getContextPath());
      out.write("/jobs/refinements/overview.jsp?job=");
      out.print(theJob.getUID());
      out.write("')\" class=\"tab_text");
      out.print(jobtab==5?"_selected":"");
      out.write("\">Refinements");
      out.write("</a>\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab_seperator\">\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab\">\n                            ");
      out.write("<a href=\"javascript:doSubmit()\" class=\"tab_text\">");
      out.print(theJob.isNew()?"Submit job":"Finished");
      out.write("</a>\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab_seperator\">\n                        ");
      out.write("</td>\n                    ");
      out.write("</tr>\n                ");
      out.write("</table>\n            ");
      out.write("</td>\n        ");
      out.write("</tr>\n        ");
      out.write("<tr>\n            ");
      out.write("<td bgcolor=\"#0000FF\" height=\"1\">\n            ");
      out.write("</td>\n        ");
      out.write("</tr>\n    ");
      out.write("</table>\n");
      out.write("\n        ");
 } else { 
      out.write("\n            ");
      out.write("<b>Override for the ");
      out.print(theJob.isProfile()?"profile":"job");
      out.write(" ");
      out.print(theJob.getJobName());
      out.write(" on domain '");
      out.print(currDomain);
      out.write("'");
      out.write("</b>\n            ");

    /**
     * An include file that handles the sub navigation of a 
     * pages allowing the override section of a given 'job'. 
     * Include where the sub navigation should be displayed.
     *
     * The following variables must exist prior to this file being included:
     *
     * String theJob - The CrawlJob being manipulated.
     * int jobtab - Which to display as 'selected'.
     *          SUPERCEDED BY SUBMODULES 1 - Filters
     *          2 - Settings
     *          SUPERCEDED BY SUBMODULES 4 - Credentials
     *          5 - Refinements
     *          7 - Submodules 
     
     * @author Kristinn Sigurdsson
     */

      out.write("\n    ");
      out.write("<table cellspacing=\"0\" cellpadding=\"0\">\n        ");
      out.write("<tr>\n            ");
      out.write("<td bgcolor=\"#0000FF\" height=\"1\">\n            ");
      out.write("</td>\n        ");
      out.write("</tr>\n        ");
      out.write("<tr>\n            ");
      out.write("<td>\n                ");
      out.write("<table cellspacing=\"0\" cellpadding=\"0\">\n                    ");
      out.write("<tr>\n                        ");
      out.write("<td>\n                            ");
      out.write("<b>Available options:");
      out.write("</b>\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab_seperator\">\n\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab");
      out.print(jobtab==7?"_selected":"");
      out.write("\" nowrap>\n                            ");
      out.write("<a href=\"javascript:doGoto('");
      out.print(request.getContextPath());
      out.write("/jobs/per/submodules.jsp?job=");
      out.print(theJob.getUID());
      out.write("')\" class=\"tab_text");
      out.print(jobtab==7?"_selected":"");
      out.write("\">Submodules");
      out.write("</a>\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab_seperator\">\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab");
      out.print(jobtab==2?"_selected":"");
      out.write("\" nowrap>\n                            ");
      out.write("<a href=\"javascript:doGoto('");
      out.print(request.getContextPath());
      out.write("/jobs/per/configure.jsp?job=");
      out.print(theJob.getUID());
      out.write("')\" class=\"tab_text");
      out.print(jobtab==2?"_selected":"");
      out.write("\">Settings");
      out.write("</a>\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab_seperator\">\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab");
      out.print(jobtab==5?"_selected":"");
      out.write("\" nowrap>\n                            ");
      out.write("<a href=\"javascript:doGoto('");
      out.print(request.getContextPath());
      out.write("/jobs/refinements/overview.jsp?job=");
      out.print(theJob.getUID());
      out.write("')\" class=\"tab_text");
      out.print(jobtab==5?"_selected":"");
      out.write("\">Refinements");
      out.write("</a>\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab_seperator\">\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab\">\n                            ");
      out.write("<a href=\"javascript:doSubmit()\" class=\"tab_text\">Done with the override");
      out.write("</a>\n                        ");
      out.write("</td>\n                        ");
      out.write("<td class=\"tab_seperator\">\n                        ");
      out.write("</td>\n                    ");
      out.write("</tr>\n                ");
      out.write("</table>\n            ");
      out.write("</td>\n        ");
      out.write("</tr>\n        ");
      out.write("<tr>\n            ");
      out.write("<td bgcolor=\"#0000FF\" height=\"1\">\n            ");
      out.write("</td>\n        ");
      out.write("</tr>\n    ");
      out.write("</table>\n");
      out.write("\n        ");
 } 
      out.write("\n    ");
      out.write("<p>\n    ");
      out.write("<form name=\"frmRefinements\" method=\"post\" action=\"overview.jsp\">\n        ");
      out.write("<input type=\"hidden\" name=\"action\" value=\"done\">\n        ");
      out.write("<input type=\"hidden\" name=\"where\" value=\"\">\n        ");
      out.write("<input type=\"hidden\" name=\"job\" value=\"");
      out.print(theJob.getUID());
      out.write("\">\n        ");
      out.write("<input type=\"hidden\" name=\"currDomain\" value=\"");
      out.print(currDomain==null?"":currDomain);
      out.write("\">\n        ");
      out.write("<input type=\"hidden\" name=\"refinement\" value=\"\">\n        ");
      out.write("<b>\n            Known Refinements for '");
      out.print(global?"global settings":currDomain);
      out.write("' of\n            ");
      out.print(theJob.isProfile()?"profile":"job");
      out.write("\n            ");
      out.print(theJob.getJobName());
      out.write(":\n        ");
      out.write("</b>\n        ");
      out.write("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"450\">\n            ");

                ListIterator refinements = localSettings.refinementsIterator();
                boolean alt = true;
                while(refinements.hasNext()){
                    Refinement ref = (Refinement)refinements.next();
            
      out.write("\n                    ");
      out.write("<tr ");
      out.print(alt?"bgcolor='#EEEEFF'":"");
      out.write(">\n                        ");
      out.write("<td width=\"350\">\n                            ");
      out.write("<b>");
      out.print(ref.getReference());
      out.write("</b>\n                        ");
      out.write("</td>\n                        ");
      out.write("<td nowrap>\n                            &nbsp;");
      out.write("<a href=\"javascript:doEdit('");
      out.print(ref.getReference());
      out.write("')\" style=\"color: #003399;\" class=\"underLineOnHover\">Edit");
      out.write("</a>\n                        ");
      out.write("</td>\n                        ");
      out.write("<td nowrap>\n                            &nbsp;");
      out.write("<a href=\"javascript:doDelete('");
      out.print(ref.getReference());
      out.write("')\" style=\"color: #003399;\" class=\"underLineOnHover\">Remove");
      out.write("</a>\n                        ");
      out.write("</td>\n                    ");
      out.write("</tr>\n                    ");
      out.write("<tr ");
      out.print(alt?"bgcolor='#EEEEFF'":"");
      out.write(">\n                        ");
      out.write("<td colspan=\"3\">\n                            ");
      out.write("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n                                ");
      out.write("<tr>\n                                    ");
      out.write("<td>&nbsp;&nbsp;&nbsp;");
      out.write("</td>\n                                    ");
      out.write("<td width=\"100%\">\n                                        ");
      out.write("<i>");
      out.print(ref.getDescription());
      out.write("</i>\n                                    ");
      out.write("</td>\n                                ");
      out.write("</tr>\n                            ");
      out.write("</table>\n                        ");
      out.write("</td>\n                    ");
      out.write("</tr>\n            ");

                    alt = !alt;
                }
            
      out.write("\n        ");
      out.write("</table>\n        ");
      out.write("<p>\n        ");
      out.write("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n            ");
      out.write("<tr>\n                ");
      out.write("<td colspan=\"2\">\n                    ");
      out.write("<b>Create new refinement");
      out.write("</b>\n                ");
      out.write("</td>\n            ");
      out.write("</tr>\n            ");
      out.write("<tr>\n                ");
      out.write("<td>\n                    Name:\n                ");
      out.write("</td>\n                ");
      out.write("<td>\n                    ");
      out.write("<input name=\"newReference\" style=\"width: 230px\">\n                ");
      out.write("</td>\n            ");
      out.write("</tr>\n            ");
      out.write("<tr>\n                ");
      out.write("<td valign=\"top\">\n                    Description:&nbsp;\n                ");
      out.write("</td>\n                ");
      out.write("<td>\n                    ");
      out.write("<textarea name=\"newDescription\" style=\"width: 230px\" rows=\"4\">");
      out.write("</textarea>\n                ");
      out.write("</td>\n            ");
      out.write("</tr>\n            ");
      out.write("<tr>\n                ");
      out.write("<td colspan=\"2\" align=\"right\">\n                    ");
      out.write("<input type=\"button\" value=\"Create\" onClick=\"newRefinement()\">\n                ");
      out.write("</td>\n        ");
      out.write("</table>\n    ");
      out.write("</form>\n");

    /**
     * An include file that handles the "look" and navigation of a web page. 
     * Wrapps up things begun in the "head.jsp" include file.  See it for
     * more details.
     *
     * @author Kristinn Sigurdsson
     */

      out.write("\n");
      out.write("<br/>\n");
      out.write("<br/>\n        ");
      out.write("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n            ");
      out.write("<tr>\n            ");
      out.write("<td bgcolor=\"#0000FF\" height=\"1\" colspan=\"4\">");
      out.write("</td>\n            ");
      out.write("</tr>\n            ");
      out.write("<tr>\n            ");
      out.write("<td class=\"instance_name\">Identifier: ");
      out.print(currentHeritrixName);
      out.write("</td>\n            ");
      out.write("</tr>\n        ");
      out.write("</table>\n                    ");
      out.write("<!-- END MAIN BODY -->\n    ");
      out.write("</body>\n");
      out.write("</html>");
      out.write("\n");
    } catch (Throwable t) {
      out = _jspx_out;
      if (out != null && out.getBufferSize() != 0)
        out.clearBuffer();
      if (pageContext != null) pageContext.handlePageException(t);
    } finally {
      if (_jspxFactory != null) _jspxFactory.releasePageContext(pageContext);
    }
  }
}
