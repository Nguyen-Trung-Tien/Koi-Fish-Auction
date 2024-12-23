import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../css/login.css';
import Header from "../components/Header";
import Footer from "../components/Footer";
import { useUser } from '../UserContext/UserContext';
import {setRole} from "../components/admin/RoleUser";

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();
    const { setUser } = useUser();

    const handleLogin = (event) => {
        event.preventDefault();
    
        fetch('http://localhost:8080/auction/users/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password }),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Mã trạng thái không phải 200'); 
            }
            return response.json();
        })
        .then(data => {
            if (data.message === "successful") {
                console.log('Đăng nhập thành công:', data.user);
                localStorage.setItem('isAuthenticated', 'true');
                localStorage.setItem('user', JSON.stringify(data.user));
                setUser(data.user); 
                navigate('/');
                setRole(data.user.role);
            } else {
                console.error('Đăng nhập thất bại:', data.message);
                setErrorMessage('Tài khoản hoặc mật khẩu không đúng');
            }
        })
        .catch(error => {
            console.error('Lỗi:', error);
            alert('Đã xảy ra lỗi, vui lòng thử lại sau!');
        });
        setTimeout(() => {
            window.location.reload();
            navigate('/');
        }, 500);
    };

    return (
        <div id="main">
            <Header />
            <div id="content">
                {/* Begin Login Form */}
                <div id="login-container">
                    <h2>Login</h2>
                    <form id="login-form" onSubmit={handleLogin}>
                        <div className="form-group">
                            <label htmlFor="username">Username</label>
                            <input
                                placeholder="Username"
                                type="text"
                                id="username"
                                name="username"
                                required
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                            />
                        </div>
                        <div className="form-group password-container">
                            <label htmlFor="password">Password</label>
                            <input
                                placeholder="Password"
                                type="password"
                                id="password"
                                name="password"
                                required
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>
                        <button type="submit" className="submit-button">Login</button>
                        {errorMessage && <div className="error-message">{errorMessage}</div>}
                    </form>

                </div>
                {/* End Login Form */}
                <Footer />
            </div>
        </div>
    );
};

export default Login;
