package controller;

import mg.itu.prom16.*;
import model.User;
import service.UserService;

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
        System.out.println("Param user reçu : " + user);
        System.out.println("CurrentSession : " + session);

        User authenticatedUser = null;
        try {
            userService.testConnection();
            authenticatedUser = userService.authenticate(user.getUsername(), user.getPassword());
            System.out.println("Résultat authenticate : " + authenticatedUser);
        } catch (Exception e) {
            System.out.println("Exception lors de authenticate :");
            e.printStackTrace();
        }

        if (authenticatedUser != null) {
            try {
                System.out.println("Ajout de l'utilisateur et du rôle dans la session...");
                session.add("user", authenticatedUser);
                session.add("role", authenticatedUser.getRole());
                System.out.println("Session après ajout : " + session);
            } catch (Exception e) {
                System.out.println("Exception lors de session.add :");
                e.printStackTrace();
            }

            ModelView mv = new ModelView("views/dashboard.jsp");
            mv.addObject("user", authenticatedUser);
            mv.addObject("message", "Connexion réussie !");
            System.out.println("=== Fin processLogin : connexion réussie ===");
            return mv;
        } else {
            System.out.println("Échec de l'authentification : utilisateur ou mot de passe incorrect");
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Nom d'utilisateur ou mot de passe incorrect");
            System.out.println("=== Fin processLogin : retour login.jsp ===");
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
        if (userService.register(user)) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("success", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            return mv;
        } else {
            ModelView mv = new ModelView("views/register.jsp");
            mv.addObject("error", "Erreur lors de l'inscription. L'utilisateur existe peut-être déjà.");
            return mv;
        }
    }

    @AnnotedMth("logout")
    public ModelView logout(CurrentSession session) {
        session.delete("user");
        session.delete("role");

        ModelView mv = new ModelView("views/login.jsp");
        mv.addObject("message", "Déconnexion réussie !");
        return mv;
    }

    @AnnotedMth("dashboard")
    public ModelView dashboard(CurrentSession session) {
        User user = (User) session.get("user");
        if (user == null) {
            ModelView mv = new ModelView("views/login.jsp");
            mv.addObject("error", "Vous devez vous connecter d'abord");
            return mv;
        }

        ModelView mv = new ModelView("views/dashboard.jsp");
        mv.addObject("user", user);
        return mv;
    }
}