import React, { createContext, useState, useContext } from 'react';
import axios from 'axios';

// создаём 'служебный канал' (контекст) для передачи данных об аутентификации.
const AuthContext = createContext(null);

// это наш компонент-провайдер. он будет 'транслировать' по каналу, кто сейчас в системе.
export const AuthProvider = ({ children }) => {
    // здесь мы храним 'личное дело' (данные) текущего агента. изначально — пусто.
    const [currentUser, setCurrentUser] = useState(null);

    // функция для 'проверки документов' (входа).
    const login = async (loginData) => {
        try {
            // отправляем запрос на сервер с логином и паролем.
            const response = await axios.post('http://localhost:8080/api/auth/login', loginData);
            // если проверка пройдена, записываем агента в currentUser.
            setCurrentUser(response.data);
            return { success: true };
        } catch (error) {
            // если нет — сообщаем об ошибке.
            console.error("ошибка входа:", error);
            return { success: false, message: error.response?.data || "ошибка сервера" };
        }
    };

    // функция 'сдачи смены' (выход). просто очищаем данные об агенте.
    const logout = () => {
        setCurrentUser(null);
    };

    // собираем в один 'пакет' и данные агента, и функции для входа/выхода.
    const value = { currentUser, login, logout };

    // делаем этот 'пакет' доступным для всех дочерних компонентов нашего приложения.
    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};

// а это наш 'универсальный ключ' (хук) для любого компонента, 
// которому нужен быстрый доступ к данным агента или функциям входа/выхода.
export const useAuth = () => {
    return useContext(AuthContext);
};