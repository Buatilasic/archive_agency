
import React, { createContext, useState, useContext } from 'react';
import axios from 'axios';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [currentUser, setCurrentUser] = useState(null);

    const login = async (loginData) => {
        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', loginData);
            setCurrentUser(response.data);
            return { success: true };
        } catch (error) {
            console.error("Ошибка входа:", error);
            return { success: false, message: error.response?.data || "Ошибка сервера" };
        }
    };

    const logout = () => {
        setCurrentUser(null);
    };

    const value = { currentUser, login, logout };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};