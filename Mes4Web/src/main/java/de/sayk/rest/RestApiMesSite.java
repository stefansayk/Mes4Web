package de.sayk.rest;

public class RestApiMesSite {

	private StringBuffer sb = new StringBuffer();

	public RestApiMesSite() {
		add("<!DOCTYPE html>");
		add("<html lang=\"de\">");
		add("  <head>");
		add("    <meta charset=\"utf-8\">");
		add("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
		add("    <title>sfmMES</title>");
		add("    <style>");
		add("        body {");
		add("            margin: 0 0 0 2em;");
		add("            color: #637e97;");
		add("            background-color: #fbfbfb;");
		add("            font-family: arial;");
		add("        }");
		add("");
		add("        form {");
		add("            width: 50em;");
		add("        }");
		add("");
		add("        text {");
		add("            font-weight: bold;");
		add("            color: #163c71;");
		add("		}");
		add("");
		add("        h1 {");
		add("            color: #da6f0f;");
		add("        }");
		add("");
		add("        label.h2 {");
		add("            font-weight: bold;");
		add("            font-size: 150%;");
		add("            width: 100%;");
		add("            margin-bottom: 1em;");
		add("        }");
		add("");
		add("        input, label {");
		add("            float: left;");
		add("            width: 20%;");
		add("        }");
		add("");
		add("        input {");
		add("            margin: 0 0 1em .2em;");
		add("            padding: .2em .5em;");
		add("            background-color: #fffbf0;");
		add("            border: 1px solid #e7c157;");
		add("        }");
		add("");
		add("        select {");
		add("            margin: 0 0 1em .2em;");
		add("            padding: .2em .5em;");
		add("            background-color: #aaaaaa;");
		add("            border: 1px solid #e7c157;");
		add("        }");
		add("");
		add("        label.input {");
		add("            text-align: right;");
		add("            line-height: 1.5;");
		add("        }");
		add("");
		add("        label.input::after {");
		add("            content: \": \";");
		add("        }");
		add("");
		add("        button {");
		add("            margin-top: 1.5em;");
		add("            width: 100%;");
		add("        }");
		add("");
		add("		.myButton {");
		add("			-moz-box-shadow:inset 0px 1px 0px 0px #ffffff;");
		add("			-webkit-box-shadow:inset 0px 1px 0px 0px #ffffff;");
		add("			box-shadow:inset 0px 1px 0px 0px #ffffff;");
		add("			background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #ededed), color-stop(1, #dfdfdf) );");
		add("			background:-moz-linear-gradient( center top, #ededed 5%, #dfdfdf 100% );");
		add("			filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#ededed', endColorstr='#dfdfdf');");
		add("			background-color:#ededed;");
		add("			-webkit-border-top-left-radius:6px;");
		add("			-moz-border-radius-topleft:6px;");
		add("			border-top-left-radius:6px;");
		add("			-webkit-border-top-right-radius:6px;");
		add("			-moz-border-radius-topright:6px;");
		add("			border-top-right-radius:6px;");
		add("			-webkit-border-bottom-right-radius:6px;");
		add("			-moz-border-radius-bottomright:6px;");
		add("			border-bottom-right-radius:6px;");
		add("			-webkit-border-bottom-left-radius:6px;");
		add("			-moz-border-radius-bottomleft:6px;");
		add("			border-bottom-left-radius:6px;");
		add("		text-indent:0;");
		add("			border:1px solid #dcdcdc;");
		add("			display:inline-block;");
		add("			color:#777777;");
		add("			font-family:arial;");
		add("			font-size:15px;");
		add("			font-weight:bold;");
		add("			font-style:normal;");
		add("		height:50px;");
		add("			line-height:50px;");
		add("		width:100px;");
		add("			text-decoration:none;");
		add("			text-align:center;");
		add("			text-shadow:1px 1px 0px #ffffff;");
		add("		}.myButton:hover {");
		add("			background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #dfdfdf), color-stop(1, #ededed) );");
		add("			background:-moz-linear-gradient( center top, #dfdfdf 5%, #ededed 100% );");
		add("			filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#dfdfdf', endColorstr='#ededed');");
		add("			background-color:#dfdfdf;");
		add("		}.myButton:active {");
		add("			position:relative;");
		add("			top:1px;");
		add("    </style>");
		add("");
		add("    <script>");
		add("        function newOrder() {");
		add("          var Prod  = document.getElementById('product');");
		add("          var Color  = document.getElementById('color');");
		add("          var Variant  = document.getElementById('variant');");
		add("          var Customer  = document.getElementById('customer');");
		add("          var Ziel = \"neworder/product/\"+Prod.value+\"/color/\"+Color.value+\"/variant/\"+Variant.value+\"/customer/\"+Customer.value;");
		add("          window.location.href = Ziel;");
		add("         }");
		add("    </script>");
		add("");
		add("  </head>");
		add("  <body>");
		add("        <h1>sfmMES</h1>");

	}

	public void add(String line) {
		sb.append(line + "\n");
	}

	public String getSiteHTML() {

		add("");
		add("  </body>");
		add("");
		add("");
		add("</html>");
		return sb.toString();
	}

}
