import React, { useState } from "react";
import axios from "axios";
import { useRouter } from "next/router";

type User = {
    id: number;
    name: string;
    email: string;
};

export default function LoginPage() {
    const router = useRouter();
    const [result, setResult] = useState<User | User[] | null>(null);
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showSearchResult, setShowSearchResult] = useState(false);
    const [loginSuccess, setLoginSuccess] = useState(false);
    const [loginError, setLoginError] = useState<string | null>(null);

    // api адрес переменной окружения
    const API_URL = process.env.NEXT_PUBLIC_API_URL;

    const handleLogin = async () => {
        try {
            const res = await axios.post(
                `${API_URL}/api/auth/login`,
                { email, password },
                { withCredentials: true }
            );
            setResult(res.data);
            setShowSearchResult(true);
            setLoginSuccess(true);
            setLoginError(null);

            router.push("/page"); // redirect after successful login
        } catch (error: any) {
            setLoginSuccess(false);
            setLoginError("Login failed. Please check your email and password.");
            setShowSearchResult(false);
            setResult(null);
            console.error("Login error:", error);
        }
    };

    const renderResult = () => {
        if (!result) return null;

        if (Array.isArray(result)) {
            return result.map((user) => (
                <div key={user.id} className="card-base p-6 mt-6">
                    <h2 className="text-xl font-semibold text-text-light">{user.name}</h2>
                    <p className="text-text-muted mt-1">{user.email}</p>
                </div>
            ));
        } else {
            return (
                <div className="card-base p-6 mt-6">
                    <h2 className="text-xl font-semibold text-text-light">{result.name}</h2>
                    <p className="text-text-muted mt-1">{result.email}</p>
                </div>
            );
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-background-primary p-6">
            <div className="card-base p-10 w-full max-w-md">
                <h1 className="text-4xl font-extrabold mb-8 text-center text-text-light">Login</h1>

                <input
                    type="text"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="Enter your email"
                    className="w-full mb-6"
                />

                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter your password"
                    className="w-full mb-8"
                />

                <button
                    onClick={handleLogin}
                    className="w-full btn-primary py-3.5 mb-4"
                >
                    Log In
                </button>

                <button
                    onClick={() => router.push("/page")}
                    className="w-full text-accent-primary hover:underline text-base py-2"
                >
                    Back to Home
                </button>

                {loginSuccess && (
                    <p className="mt-6 text-accent-success text-center font-semibold text-lg">
                        Login successful!
                    </p>
                )}

                {loginError && (
                    <p className="mt-6 text-accent-danger text-center font-semibold text-lg">
                        {loginError}
                    </p>
                )}

                {showSearchResult && renderResult()}
            </div>
        </div>
    );
}
