package controller;

import mg.itu.prom16.*;
import model.User;
import service.UserService;
import util.DatabaseUtil;

@AnnotedController
public class LoginController {
    private UserService userService = new UserService();

    @AnnotedMth("login")
    public ModelView showLoginPage() {
        return new ModelView("views/login.jsp");
    }

    @POST("processLogin")
    @AnnotedMth("processLogin")
    public ModelView processLogin(@Param(name = "user") User user, CurrentSession session) {
        System.out.println("=== Début processLogin ===");
        System.out.println("Username reçu : " + (user != null ? user.getUsername() : "null"));

        // Test de connexion (optionnel, pour debug)
        if (!DatabaseUtil.testConnection()) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Erreur de connexion à la base de données");
            return mv;
        }

        User authenticatedUser = null;
        try {
            authenticatedUser = userService.authenticate(user.getUsername(), user.getPassword());
        } catch (Exception e) {
            System.out.println("Exception lors de authenticate :");
            e.printStackTrace();
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Erreur interne. Consultez les logs.");
            return mv;
        }

        if (authenticatedUser != null) {
            try {
                System.out.println("✅ Authentification réussie pour : " + authenticatedUser.getUsername());

                // Ajout en session
                session.add("user", authenticatedUser);
                session.add("role", authenticatedUser.getRole());
                session.add("userId", authenticatedUser.getId());

                System.out.println("✅ Session mise à jour");

                // Rediriger vers la liste des vols après connexion réussie
                ModelView mv = new ModelView("vols");
                session.add("successMessage", "Connexion réussie ! Bienvenue " + authenticatedUser.getUsername());

                return mv;

            } catch (Exception e) {
                System.out.println("❌ Exception lors de la gestion de session :");
                e.printStackTrace();

                ModelView mv = new ModelView("views/login.jsp");
                mv.addObject("error", "Erreur lors de la création de la session");
                return mv;
            }
        } else {
            System.out.println("❌ Échec de l'authentification");
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Nom d'utilisateur ou mot de passe incorrect");
            return mv;
        }
    }

    @AnnotedMth("register")
    public ModelView showRegisterPage() {
        return new ModelView("views/register.jsp");
    }

    @POST("processRegister")
    @AnnotedMth("processRegister")
    public ModelView processRegister(@Param(name = "user") User user) {
        System.out.println("=== Début processRegister ===");
        System.out.println("Username : " + (user != null ? user.getUsername() : "null"));
        System.out.println("Email : " + (user != null ? user.getEmail() : "null"));

        try {
            if (userService.register(user)) {
                ModelView mv = new ModelView("views/login.jsp");
                mv.addObject("success",
                        "Inscription réussie ! Vous pouvez maintenant vous connecter avec vos identifiants.");
                return mv;
            } else {
                ModelView mv = new ModelView("views/register.jsp");
                mv.addObject("error", "Erreur lors de l'inscription. L'utilisateur ou l'email existe peut-être déjà.");
                mv.addObject("user", user); // Pour repeupler le formulaire
                return mv;
            }
        } catch (Exception e) {
            System.out.println("❌ Exception lors de processRegister :");
            e.printStackTrace();

            ModelView mv = new ModelView("views/register.jsp");
            mv.addObject("error", "Erreur interne lors de l'inscription. Consultez les logs.");
            mv.addObject("user", user);
            return mv;
        }
    }

    @AnnotedMth("logout")
    public ModelView logout(CurrentSession session) {
        System.out.println("=== Début logout ===");

        try {
            // Récupérer le nom d'utilisateur avant de supprimer la session
            User user = (User) session.get("user");
            String username = (user != null) ? user.getUsername() : "Utilisateur inconnu";

            // Nettoyer la session
            session.delete("user");
            session.delete("role");
            session.delete("userId");

            System.out.println("✅ Session nettoyée pour : " + username);

            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("message", "Déconnexion réussie ! À bientôt.");
            return mv;

        } catch (Exception e) {
            System.out.println("❌ Erreur lors du logout :");
            e.printStackTrace();

            // Même en cas d'erreur, on redirige vers login
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("message", "Déconnexion effectuée.");
            return mv;
        }
    }

    // Méthode utilitaire pour debug (optionnel)
    @AnnotedMth("dbinfo")
    public ModelView showDatabaseInfo() {
        DatabaseUtil.printConnectionInfo();
        boolean connectionOk = DatabaseUtil.testConnection();

        ModelView mv = new ModelView("views/login.jsp");
        if (connectionOk) {
            mv.addObject("message", "Base de données accessible ✅");
        } else {
            mv.addObject("error", "Problème de connexion à la base ❌");
        }
        return mv;
    }
}