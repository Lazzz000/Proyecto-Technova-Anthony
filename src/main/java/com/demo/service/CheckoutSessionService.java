package com.demo.service;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.demo.modelo.CheckoutDTO;
import com.demo.modelo.Usuario;

import jakarta.servlet.http.HttpSession;
@Service
public class CheckoutSessionService {

    private static final String CHECKOUT_SESSION_KEY = "checkoutDTO";

    public CheckoutDTO obtenerCheckout(HttpSession session) {

        CheckoutDTO checkout = 
            (CheckoutDTO) session.getAttribute(CHECKOUT_SESSION_KEY);

        if (checkout == null) {
            checkout = new CheckoutDTO();
            session.setAttribute(CHECKOUT_SESSION_KEY, checkout);
        }

        return checkout;
    }

    public void guardarCheckout(HttpSession session, CheckoutDTO checkout) {
        session.setAttribute(CHECKOUT_SESSION_KEY, checkout);
    }

    public void limpiarCheckout(HttpSession session) {
        session.removeAttribute(CHECKOUT_SESSION_KEY);
    }
}



  