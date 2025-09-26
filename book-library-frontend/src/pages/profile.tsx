"use client";

import React, { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/router";
import { loadStripe } from "@stripe/stripe-js";
import {
    Elements,
    CardNumberElement,
    CardExpiryElement,
    CardCvcElement,
    useStripe,
    useElements
} from "@stripe/react-stripe-js";

const API_URL = process.env.NEXT_PUBLIC_API_URL!;
const STRIPE_KEY = process.env.NEXT_PUBLIC_STRIPE_KEY!;

type User = {
    id: number;
    name: string;
    email: string;
    credits: number;
    role: string;
};

type Book = {
    id: number;
    title: string;
    author: string;
    description: string;
    availableCopies: number;
    status: boolean;
    takenByUser: boolean;
};

type DepositModalProps = {
    userId: number;
    onClose: () => void;
    onSuccess: (amount: number) => void;
};

function DepositModal({ userId, onClose, onSuccess }: DepositModalProps) {
    const stripe = useStripe();
    const elements = useElements();
    const [amount, setAmount] = useState<number>(0);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        console.log("Stripe:", stripe, "Elements:", elements);
    }, [stripe, elements]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!stripe || !elements) return;

        const cardNumber = elements.getElement(CardNumberElement);
        if (!cardNumber) return;

        setLoading(true);
        setMessage("");

        try {
            const { token, error } = await stripe.createToken(cardNumber);
            console.log("Generated Token:", token);  // Логируем токен
            if (error || !token) {
                setMessage(error?.message || "Token creation failed");
                setLoading(false);
                return;
            }

            const res = await axios.post(
                `${API_URL}/api/payments/deposit`,
                {
                    amount: amount * 100,
                    currency: "usd",
                    token: token.id
                },
                { withCredentials: true }
            );

            setMessage("Payment successful!");
            onSuccess(amount);
            onClose();
        } catch (err) {
            console.error(err);
            setMessage("Payment failed");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black/50 flex justify-center items-center z-50">
            <div className="bg-background-primary p-6 rounded-lg w-96 shadow-lg">
                <h2 className="text-xl font-bold mb-4 text-center">Deposit Credits</h2>
                <form onSubmit={handleSubmit} className="space-y-3">
                    <div>
                        <label className="block text-sm font-medium mb-1">Amount USD</label>
                        <input
                            type="number"
                            value={amount}
                            onChange={(e) => setAmount(Number(e.target.value))}
                            placeholder="Amount USD"
                            min={1}
                            className="border p-2 rounded w-full text-black"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium mb-1">Card Number</label>
                        <div className="border p-2 rounded">
                            <CardNumberElement options={{ placeholder: "Card number" }} />
                        </div>
                    </div>
                    <div className="flex gap-2">
                        <div className="flex-1">
                            <label className="block text-sm font-medium mb-1">Expiration Date</label>
                            <div className="border p-2 rounded">
                                <CardExpiryElement options={{ placeholder: "MM/YY" }} />
                            </div>
                        </div>
                        <div className="flex-1">
                            <label className="block text-sm font-medium mb-1">CVV</label>
                            <div className="border p-2 rounded">
                                <CardCvcElement options={{ placeholder: "CVC" }} />
                            </div>
                        </div>
                    </div>
                    <div className="flex justify-between mt-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="btn-secondary px-4 py-2"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={loading || !amount}
                            className="btn-primary px-4 py-2"
                        >
                            {loading ? "Processing..." : "Pay"}
                        </button>
                    </div>
                    {message && <p className="mt-2 text-center">{message}</p>}
                </form>
            </div>
        </div>
    );
}

export default function ProfilePage() {
    const router = useRouter();

    const [user, setUser] = useState<User | null>(null);
    const [books, setBooks] = useState<Book[]>([]);
    const [loadingUser, setLoadingUser] = useState(true);
    const [loadingBooks, setLoadingBooks] = useState(true);
    const [userError, setUserError] = useState("");
    const [booksError, setBooksError] = useState("");
    const [returningBookId, setReturningBookId] = useState<number | null>(null);
    const [showDepositModal, setShowDepositModal] = useState(false);

    const stripePromise = loadStripe(STRIPE_KEY);

    // --- Загрузка пользователя
    useEffect(() => {
        const fetchUser = async () => {
            try {
                const res = await axios.get<User>(`${API_URL}/api/users/me`, { withCredentials: true });
                setUser(res.data);
            } catch {
                setUser(null);
                setUserError("Unauthorized. Please log in.");
            } finally {
                setLoadingUser(false);
            }
        };
        fetchUser();
    }, []);

    // --- Редирект если не авторизован
    useEffect(() => {
        if (!loadingUser && !user && userError) router.push("/login_page");
    }, [loadingUser, user, userError, router]);

    // --- Загрузка книг
    useEffect(() => {
        if (loadingUser || !user) return;
        const fetchBooks = async () => {
            try {
                const res = await axios.get<Book[]>(`${API_URL}/api/users/my_books`, { withCredentials: true });
                setBooks(res.data);
            } catch {
                setBooksError("Failed to load your books");
            } finally {
                setLoadingBooks(false);
            }
        };
        fetchBooks();
    }, [user, loadingUser]);

    // --- Возврат книги
    const handleReturn = async (bookId: number) => {
        setReturningBookId(bookId);
        try {
            await axios.put(`${API_URL}/api/transaction/return`, { bookId }, { withCredentials: true });
            setBooks((prev) => prev.filter((b) => b.id !== bookId));
            const res = await axios.get<User>(`${API_URL}/api/users/me`, { withCredentials: true });
            setUser(res.data);
            alert("Book successfully returned!");
        } catch {
            alert("Failed to return the book.");
        } finally {
            setReturningBookId(null);
        }
    };

    const handleDepositSuccess = (amountAdded: number) => {
        if (user) setUser({ ...user, credits: user.credits + amountAdded });
    };

    if (loadingUser) return <div>Loading user data...</div>;
    if (userError && !user) return <div>{userError}</div>;

    return (
        <Elements stripe={stripePromise}>
            <div className="min-h-screen bg-background-primary p-8">
                <div className="max-w-4xl mx-auto">
                    {/* --- Профиль пользователя --- */}
                    <section className="card-base p-10 mb-10 text-center">
                        <h1 className="text-4xl font-extrabold mb-4 text-text-light">Your Profile</h1>
                        <p>Hello, <strong>{user?.name}</strong>!</p>
                        <p>{user?.email}</p>
                        <p className="mt-2">Balance: <span className="text-accent-success font-semibold">${user?.credits.toFixed(2)}</span></p>

                        <div className="mt-6 flex justify-center gap-4 flex-wrap">
                            <button onClick={() => router.push("/page")} className="px-8 py-3 btn-primary text-lg">Go to Main Page</button>
                            {user?.role === "ADMIN" && (
                                <button onClick={() => router.push("/admin_page")} className="px-8 py-3 btn-accent text-lg">Go to Admin Panel</button>
                            )}
                            <button
                                onClick={() => setShowDepositModal(true)}
                                className="px-8 py-3 btn-secondary text-lg"
                            >
                                Deposit Credits
                            </button>
                        </div>
                    </section>

                    {/* --- Модальное окно депозита --- */}
                    {showDepositModal && user && (
                        <DepositModal
                            userId={user.id}
                            onClose={() => setShowDepositModal(false)}
                            onSuccess={handleDepositSuccess}
                        />
                    )}

                    {/* --- Книги пользователя --- */}
                    <section className="card-base p-10">
                        <h2 className="text-3xl font-bold mb-8 text-text-light text-center">Your Borrowed Books</h2>
                        {loadingBooks && <p>Loading your books...</p>}
                        {booksError && <p>{booksError}</p>}
                        {!loadingBooks && books.length === 0 && <p>You currently have no borrowed books.</p>}
                        <ul className="space-y-6">
                            {books.map((book) => (
                                <li key={book.id} className="card-base p-6 flex justify-between items-center">
                                    <div>
                                        <h3>{book.title}</h3>
                                        <p>Author: {book.author}</p>
                                    </div>
                                    <button
                                        onClick={() => handleReturn(book.id)}
                                        disabled={returningBookId === book.id}
                                        className="btn-danger px-6 py-2.5 font-medium"
                                    >
                                        {returningBookId === book.id ? "Returning..." : "Return"}
                                    </button>
                                </li>
                            ))}
                        </ul>
                    </section>
                </div>
            </div>
        </Elements>
    );
}