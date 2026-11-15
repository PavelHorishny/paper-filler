package app.paper_filler.form;

import jakarta.servlet.http.HttpSession;

public final class FormSession {
    private static final String KEY = "clientForm";

    private FormSession(){}

    public static ClientForm get(HttpSession session){
        return (ClientForm) session.getAttribute(KEY);
    }

    public static void save(HttpSession session, ClientForm form){
        session.setAttribute(KEY,form);
    }

    public static void clear(HttpSession session){
        session.removeAttribute(KEY);
    }
}
