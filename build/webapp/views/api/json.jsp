<%@ page contentType="application/json;charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="model.PrixDetail" %>

<%
    Map<String, Object> map = (Map<String, Object>) request.getAttribute("data");

    if (map != null) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (i++ > 0) sb.append(",");

            String key = entry.getKey();
            Object value = entry.getValue();

            sb.append("\"").append(key).append("\":");

            if (value instanceof Number || value instanceof Boolean) {
                sb.append(value.toString());
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                sb.append("[");
                int j = 0;
                for (Object obj : list) {
                    if (j++ > 0) sb.append(",");
                    if (obj instanceof PrixDetail) {
                        PrixDetail d = (PrixDetail) obj;
                        sb.append("{")
                          .append("\"categorieAgeId\":").append(d.getCategorieAgeId()).append(",")
                          .append("\"typeSiege\":\"").append(d.getTypeSiege()).append("\",")
                          .append("\"quantite\":").append(d.getQuantite()).append(",")
                          .append("\"prixUnitaire\":").append(d.getPrixUnitaire()).append(",")
                          .append("\"prixTotal\":").append(d.getPrixTotal())
                          .append("}");
                    }
                }
                sb.append("]");
            } else {
                sb.append("\"").append(value != null ? value.toString().replace("\"", "\\\"") : "").append("\"");
            }
        }
        sb.append("}");
        out.print(sb.toString());
    } else {
        out.print("{\"success\":false,\"error\":\"Aucune donnée\"}");
    }
%>