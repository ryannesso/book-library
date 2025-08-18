import React, { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/router";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

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

export default function ProfilePage() {
    const router = useRouter();

    const [user, setUser] = useState<User | null>(null);
    const [books, setBooks] = useState<Book[]>([]);
    const [loadingUser, setLoadingUser] = useState(true);
    const [loadingBooks, setLoadingBooks] = useState(true);
    const [userError, setUserError] = useState("");
    const [booksError, setBooksError] = useState("");
    const [returningBookId, setReturningBookId] = useState<number | null>(null);
    const [mounted, setMounted] = useState(false);

    // --- Монтирование (для гидратации)
    useEffect(() => {
        setMounted(true);
    }, []);

    // --- Загрузка данных пользователя
    useEffect(() => {
        const fetchUser = async () => {
            try {
                const res = await axios.get<User>(`${API_URL}/api/users/me`, {
                    withCredentials: true,
                });
                setUser(res.data);
            } catch (err) {
                console.error("Failed to load user", err);
                setUser(null);
                setUserError("Unauthorized. Please log in.");
            } finally {
                setLoadingUser(false);
            }
        };
        fetchUser();
    }, []);

    // --- Если пользователь реально не авторизован → редиректим
    useEffect(() => {
        if (!loadingUser && !user && userError) {
            router.push("/login_page");
        }
    }, [loadingUser, user, userError, router]);

    // --- Загрузка книг пользователя
    useEffect(() => {
        if (loadingUser || !user) return;

        const fetchBooks = async () => {
            try {
                const res = await axios.get<Book[]>(`${API_URL}/api/users/my_books`, {
                    withCredentials: true,
                });
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
            await axios.put(
                `${API_URL}/api/transaction/return`,
                { bookId },
                { withCredentials: true }
            );

            // Обновляем книги
            setBooks((prevBooks) => prevBooks.filter((book) => book.id !== bookId));

            // Обновляем баланс пользователя
            const res = await axios.get<User>(`${API_URL}/api/users/me`, {
                withCredentials: true,
            });
            setUser(res.data);

            alert("Book successfully returned!");
        } catch (err) {
            console.error("Return failed", err);
            alert("Failed to return the book. Please try again.");
        } finally {
            setReturningBookId(null);
        }
    };

    if (loadingUser) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-background-primary text-text-light text-xl">
                Loading user data...
            </div>
        );
    }

    if (userError && !user) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-background-primary text-accent-danger text-xl">
                {userError}
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-background-primary p-8">
            <div className="max-w-4xl mx-auto">
                {/* --- Профиль пользователя --- */}
                <section className="card-base p-10 mb-10 text-center">
                    <h1 className="text-4xl font-extrabold mb-4 text-text-light">
                        Your Profile
                    </h1>
                    <p className="text-xl text-text-light mb-1">
                        Hello,{" "}
                        <strong className="text-accent-primary">{user?.name}</strong>!
                    </p>
                    <p className="text-text-muted text-lg">{user?.email}</p>
                    <p className="text-text-muted text-lg mt-2">
                        Balance:{" "}
                        <span className="text-accent-success font-semibold">
                            ${user?.credits.toFixed(2)}
                        </span>
                    </p>
                    <div className="mt-8 flex justify-center gap-4 flex-wrap">
                        <button
                            onClick={() => router.push("/page")}
                            className="px-8 py-3 btn-primary text-lg"
                        >
                            Go to Main Page
                        </button>

                        {mounted && user?.role === "ADMIN" && (
                            <button
                                onClick={() => router.push("/admin_page")}
                                className="px-8 py-3 btn-accent text-lg"
                            >
                                Go to Admin Panel
                            </button>
                        )}
                    </div>
                </section>

                {/* --- Книги пользователя --- */}
                <section className="card-base p-10">
                    <h2 className="text-3xl font-bold mb-8 text-text-light text-center">
                        Your Borrowed Books
                    </h2>

                    {loadingBooks && (
                        <p className="text-text-muted text-center text-lg">
                            Loading your books...
                        </p>
                    )}
                    {booksError && (
                        <p className="text-accent-danger text-center text-lg">{booksError}</p>
                    )}
                    {!loadingBooks && books.length === 0 && (
                        <p className="text-text-muted text-center text-lg">
                            You currently have no borrowed books.
                        </p>
                    )}

                    <ul className="space-y-6">
                        {books.map((book) => (
                            <li
                                key={book.id}
                                className="card-base p-6 transition-all duration-300 hover:shadow-xl hover:border-accent-primary transform hover:-translate-y-0.5 flex flex-col sm:flex-row justify-between items-start sm:items-center"
                            >
                                <div className="flex-grow mb-4 sm:mb-0">
                                    <h3 className="text-2xl font-bold text-text-light">
                                        {book.title}
                                    </h3>
                                    <p className="text-base text-text-muted mt-1">
                                        Author: {book.author}
                                    </p>
                                    <p className="text-sm text-text-muted mt-2 max-w-lg line-clamp-2">
                                        {book.description}
                                    </p>
                                </div>
                                <button
                                    onClick={() => handleReturn(book.id)}
                                    disabled={returningBookId === book.id}
                                    className="btn-danger px-6 py-2.5 font-medium text-base sm:ml-6"
                                >
                                    {returningBookId === book.id ? "Returning..." : "Return"}
                                </button>
                            </li>
                        ))}
                    </ul>
                </section>
            </div>
        </div>
    );
}
