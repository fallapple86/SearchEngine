<%@ page import="com.src.searchengine.ResultElement" %><%--
  Created by IntelliJ IDEA.
  User: qpan
  Date: 11/7/2016
  Time: 9:51 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.ArrayList" %>
<html>
<head>
  <script type="text/javascript" src="js/jquery-3.1.0.slim.js"></script>
  <title>QiupingPan's Search Engine</title>
  <style>
    em{
      font-weight: bold;
      font-style: normal;
    }
  </style>
</head>
<body style="margin: 0; width: 100%; overflow-y: scroll">
<div>
  <div id="head" style="height: 69px; background: #f1f1f1; left: 0; position: absolute; width: 100%">
    <form action="/Search" style="max-width: 784px; width: auto; position:relative; background: none; margin-top: 7px" method="get" onsubmit="return q.value!=''">
      <div style="margin: -1px 0 0; padding-right: 0; padding-left: 126px; position: relative;">
        <div id="logo" style="position: absolute; padding-left: 13px; padding-right: 12px; padding-top: 6px; left: 0; z-index: 1">
          <img src="sources/logo.png" height="41" width="95">
        </div>
        <div id="search_block" style="border: 1px solid #d9d9d9; width: 638px; height: 38px; position: relative; background-color: white; top: 6px">
          <div style="position: relative; line-height: 0; text-align: left; max-width: 600px; ">
            <%
              Object parameter = request.getParameter("q");
              if(parameter == null){
                parameter = "";
              }
            %>
            <input id="query" name = 'q' type="text" value="<%=parameter%>" alt="Search" style="border:none; padding: 0px; margin: 9px 6px; width:100%; position:absolute; font: 16px arial, sans-serif; line-height: 26px !important; height: 26px !important; z-index: 6; left: 0; outline: none">
          </div>
          <div style="height: 40px; width: 40px; min-width: 38px !important; background-color: #4285f4; border: none; margin: 0; padding: 0; text-align: left; position: relative; left: 606px">
            <button style="background: url(sources/search.png) no-repeat; margin:6px; border: 0px; height: 30px; outline: 0; width: 100%; padding: 1px 6px; text-align: center; cursor: hand;">
            </button>
          </div>
        </div>
      </div>
    </form>
  </div>
  <div id="body" style="position: relative; top: 75px; clear:both; zoom: 1; ">
    <div style="position: relative;">
      <div style="background: white; min-width: 980px; height: 43px; margin-left: 120px; ">
        <div style="line-height: 43px; position: absolute; top: 0; color: #808080; padding-left: 16px; padding-top: 0; padding-bottom: 0; padding-right: 8px;">
          <%
            Object size = request.getAttribute("Size");
            if(size != null && (Integer)size != 0){
          %>
          About <%=size%> results (<%=request.getAttribute("Time")%> seconds)
          <%
            }
          %>
        </div>
      </div>
    </div>
    <div style="position: relative;">
      <div style="float: left; margin-left: 120px; width: 616px; margin-top: 3px; padding: 0 8px; margin-right: 254px; font-size: medium; font-weight: normal; font-family: Arial, sans-serif; color: #222">
        <%
          String value = (String )request.getAttribute("query");
          if(size != null && (Integer)size == 0){
        %>
        <p style="padding-top: .33em;">
          You search - <em><%= value%></em> - did not match any documents.
        </p>
        <p style="margin-top: 1em">Suggestions: </p>
        <ul style="margin-left: 1.3em; margin-bottom: 2em;">
          <li>Make sure all words are spelled correctly.</li>
          <li>Try different keywords.</li>
          <li>Try more general keywords.</li>
        </ul>
        <%
        }
        else if(size != null && (Integer)size > 0){
          DecimalFormat form = new DecimalFormat("0.00");
          ArrayList<ResultElement> resultElements = (ArrayList<ResultElement>) request.getAttribute("Results");
          for (ResultElement element: resultElements) {
        %>
        <div style="line-height: 1.2; text-align: left; font-weight: normal; margin-top: 0; margin-bottom: 23px;">
          <div style="position: relative; font-family: arial, sans-serif; font-size: small">
            <h3 style="font-size: 18px; font-weight: normal; margin: 0; padding: 0;">
              <a style="text-decoration: none; cursor:pointer; " href="<%=element.Url%>"><%=element.Name%></a>
            </h3>
          </div>
          <div style="max-width: 48em; color: #545454; line-height: 18px; text-align: left">
            <div style="white-space: nowrap; height: 17px; line-height: 16px; color: #808080;">
              <cite style="color: #006621; font-style: normal; font-size: 14px;"><%=element.Url%></cite>
            </div>
            <span style="margin-right: 20px;">
                  Weight: <b><%= form.format(element.TermFrequency)%></b>
                </span>
            <span>
                  Key words: <em style="color: #6a6a6a"><%= element.Matches%></em>
                </span>
          </div>
        </div>
        <%
          }
        %>
        <%
          }
        %>
      </div>
    </div>
    <script type="application/javascript">
      var body = document.body, html = document.documentElement;
      var height = Math.max( body.scrollHeight, body.offsetHeight, html.clientHeight, html.scrollHeight, html.offsetHeight) - 75;
      $("#body").css("height", height);
    </script>
  </div>
  <div style="position: relative; height: auto; left: 0; right: 0; padding-top: 18px; background: #f2f2f2; border-top: 1px solid #e4e4e4; height: auto; line-height: 40px; min-width: 980px;">
    <div style="color: #aaa; margin-left: 135px; line-height: 15px;">
      <span style="color: #aaa; line-height: 15px; font-family: arial, sans-serif">University of Arkansas, Fayetteville, AR</span>
    </div>
    <div style="color: #777; margin-left: 135px; line-height: 40px;">
      <span style="color: #777; line-height: 40px; font-family: arial, sans-serif; font-size: small" >CSCE 5533- Advanced Information Retrieval Homework 4</span>
    </div>
  </div>
</div>
<script type="application/javascript">

  $(document).ready(function () {
    $("#query").focus();
    $("#query").val($("#query").val());
  });
</script>
</body>
</html>
