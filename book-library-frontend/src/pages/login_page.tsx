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

    const handleLogin = async () => {
        try {
            const res = await axios.post(
                `http://localhost:8081/api/auth/login`,
                { email, password },
                { withCredentials: true }
            );
            setResult(res.data);
            setShowSearchResult(true);
            setLoginSuccess(true);
            setLoginError(null);

            router.push("/page"); // перенаправление после успешного входа
        } catch (error: any) {
            setLoginSuccess(false);
            setLoginError("Ошибка при входе. Проверьте имя и пароль.");
            setShowSearchResult(false);
            setResult(null);
            console.error("Login error:", error);
        }
    };

    const renderResult = () => {
        if (!result) return null;

        if (Array.isArray(result)) {
            return result.map((user) => (
                <div key={user.id} className="bg-gray-100 p-4 rounded shadow mt-4">
                    <h2 className="text-xl font-semibold">{user.name}</h2>
                    <p className="text-gray-700">{user.email}</p>
                </div>
            ));
        } else {
            return (
                <div className="bg-gray-100 p-4 rounded shadow mt-4">
                    <h2 className="text-xl font-semibold">{result.name}</h2>
                    <p className="text-gray-700">{result.email}</p>
                </div>
            );
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-200">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
                <h1 className="text-2xl font-bold mb-6 text-center">Login</h1>

                <input
                    type="text"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="Enter your name"
                    className="w-full px-4 py-2 border rounded mb-4 focus:outline-none focus:ring-2 focus:ring-yellow-400"
                />

                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter your password"
                    className="w-full px-4 py-2 border rounded mb-4 focus:outline-none focus:ring-2 focus:ring-yellow-400"
                />

                <button
                    onClick={handleLogin}
                    className="w-full bg-yellow-400 hover:bg-yellow-500 text-white font-semibold py-2 rounded mb-2 transition"
                >
                    Log In
                </button>

                <button
                    onClick={() => router.push("/page")}
                    className="w-full text-blue-600 hover:underline text-sm"
                >
                    Back to Home
                </button>

                {loginSuccess && (
                    <p className="mt-4 text-green-600 text-center font-semibold">
                        Login successful!
                    </p>
                )}

                {loginError && (
                    <p className="mt-4 text-red-600 text-center font-semibold">
                        {loginError}
                    </p>
                )}

                {showSearchResult && renderResult()}
            </div>
        </div>
    );
}
