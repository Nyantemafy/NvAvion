<%@ page contentType="application/pdf" %>
<%@ page import="java.io.OutputStream" %>
<%
    // Récupérer les données du PDF depuis le ModelView
    byte[] pdfContent = (byte[]) request.getAttribute("pdfContent");
    String fileName = (String) request.getAttribute("fileName");
    Integer contentLength = (Integer) request.getAttribute("contentLength");
    
    if (pdfContent != null && fileName != null) {
        // Configurer les headers pour le téléchargement
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Content-Length", String.valueOf(contentLength));
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setDateHeader("Expires", 0);
        
        // Écrire le contenu PDF dans la réponse
        try (OutputStream out = response.getOutputStream()) {
            out.write(pdfContent);
            out.flush();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du PDF: " + e.getMessage());
        }
    } else {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la génération du PDF");
    }
%>