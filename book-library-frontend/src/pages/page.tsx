import React, { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/router";

type Book = {
    id: number;
    title: string;
    author: string;
    description: string;
    status: boolean;
    availableCopies: number;
    price: number;
};

type User = {
    id: number;
    name: string;
    email: string;
};

export default function Page() {
    const router = useRouter();
    const [books, setBooks] = useState<Book[]>([]);
    const [userBookIds, setUserBookIds] = useState<number[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [query, setQuery] = useState("");
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    const API_URL = process.env.NEXT_PUBLIC_API_URL;

    // Проверка авторизации
    useEffect(() => {
        axios
            .get<User>(`${API_URL}/api/users/me`, { withCredentials: true })
            .then((res) => {
                if (res.data?.id) setIsAuthenticated(true);
                else setIsAuthenticated(false);
            })
            .catch(() => setIsAuthenticated(false));
    }, [API_URL]);

    // Загрузка всех книг
    useEffect(() => {
        fetchBooks();
    }, [API_URL]);

    // Загрузка книг пользователя
    useEffect(() => {
        if (!isAuthenticated) return;
        axios
            .get<Book[]>(`${API_URL}/api/users/my_books`, { withCredentials: true })
            .then((res) => {
                const ids = res.data.map((book) => book.id);
                setUserBookIds(ids);
            })
            .catch((err) => console.error("Error fetching user books:", err));
    }, [isAuthenticated, API_URL]);

    const fetchBooks = async () => {
        setLoading(true);
        try {
            const response = await axios.get(`${API_URL}/api/books/all`);
            setBooks(response.data);
            setError(null);
        } catch (e) {
            setError("Error loading books");
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async () => {
        if (!query.trim()) {
            fetchBooks();
            return;
        }
        setLoading(true);
        try {
            const response = await axios.get(`${API_URL}/api/books/title`, {
                params: { title: query },
            });
            setBooks(response.data);
            setError(null);
        } catch (e) {
            setError("Error during search");
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = async () => {
        try {
            await axios.post(
                `${API_URL}/api/auth/logout`,
                {},
                { withCredentials: true }
            );
            setIsAuthenticated(false);
            setUserBookIds([]);
            setBooks([]);
            router.push("/page");
        } catch (error) {
            console.error("Logout error", error);
        }
    };

    const handleBorrow = async (bookId: number) => {
        if (!isAuthenticated) {
            alert("Please log in to borrow books.");
            router.push("/login_page");
            return;
        }
        try {
            await axios.post(
                `${API_URL}/api/transaction/borrow`,
                { bookId },
                { withCredentials: true }
            );
            alert("Book successfully borrowed!");
            setUserBookIds((prev) => [...prev, bookId]);
            setBooks((prevBooks) =>
                prevBooks.map((book) =>
                    book.id === bookId
                        ? { ...book, availableCopies: book.availableCopies - 1 }
                        : book
                )
            );
        } catch (err: any) {
            if (err.response && err.response.status === 400) {
                alert(
                    err.response.data ||
                    "This book is not available or you have already borrowed it."
                );
            } else {
                alert("Failed to borrow book. Please try again.");
            }
            console.error("Error borrowing book", err);
        }
    };

    return (
        <div className="bg-background-primary min-h-screen">
            {/* Header */}
            <div className="w-full h-[80px] bg-background-secondary flex items-center justify-between px-8 shadow-2xl z-10 relative">
                <h1 className="text-text-light text-3xl font-extrabold tracking-wider">
                    Online Library
                </h1>

                <div className="flex items-center gap-6">
                    <button
                        onClick={() => router.push("/page")}
                        className="text-text-light hover:text-accent-primary px-3 py-2 rounded-md transition-colors text-lg"
                    >
                        Browse
                    </button>
                    {isAuthenticated && (
                        <button
                            onClick={() => router.push("/profile")}
                            className="text-text-light hover:text-accent-primary px-3 py-2 rounded-md transition-colors text-lg"
                        >
                            My Profile
                        </button>
                    )}

                    <input
                        type="text"
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                        placeholder="Search by title..."
                        className="px-4 py-2.5 w-72 bg-background-primary border border-border-color text-text-light focus:ring-accent-primary"
                    />
                    <button
                        onClick={handleSearch}
                        className="btn-primary py-2.5 px-6"
                    >
                        Search
                    </button>

                    {isAuthenticated ? (
                        <button
                            onClick={handleLogout}
                            className="btn-danger ml-6 py-2.5 px-6"
                        >
                            Logout
                        </button>
                    ) : (
                        <>
                            <button
                                onClick={() => router.push("/login_page")}
                                className="btn-primary ml-6 py-2.5 px-6"
                            >
                                Log-in
                            </button>
                            <button
                                onClick={() => router.push("/sign_page")}
                                className="btn-success py-2.5 px-6"
                            >
                                Sign-in
                            </button>
                        </>
                    )}
                </div>
            </div>

            {/* Book List */}
            <div className="max-w-5xl mx-auto p-8 mt-10">
                <h2 className="text-4xl font-extrabold mb-8 text-text-light text-center">
                    Available Books
                </h2>

                {loading && (
                    <p className="text-text-muted text-center text-lg">
                        Loading books...
                    </p>
                )}
                {error && (
                    <p className="text-accent-danger text-center text-lg">
                        {error}
                    </p>
                )}
                {!loading && !error && books.length === 0 && (
                    <p className="text-text-muted text-center text-lg">
                        No books found.
                    </p>
                )}

                <div className="max-h-[70vh] overflow-y-auto pr-4">
                    <ul className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {books.map((book) => (
                            <li
                                key={book.id}
                                className="card-base p-6 transition-all duration-300 hover:shadow-2xl hover:border-accent-primary transform hover:-translate-y-1"
                            >
                                <div className="flex justify-between items-start mb-3">
                                    <h3 className="text-2xl font-bold text-text-light leading-tight">
                                        {book.title}
                                    </h3>
                                    <span className="text-text-muted text-sm px-2 py-1 rounded-full bg-background-primary border border-border-color">
                                        ID: {book.id}
                                    </span>
                                </div>
                                <p className="text-text-muted mt-2 text-base">
                                    Author: {book.author}
                                </p>
                                <p className="text-text-muted mt-1 text-sm line-clamp-2">
                                    {book.description}
                                </p>
                                <p className="text-text-light mt-2 text-base">
                                    Price:{" "}
                                    <span className="font-semibold">
                                        ${book.price}
                                    </span>
                                </p>
                                <p className="text-text-light text-base">
                                    Copies available:{" "}
                                    <span className="font-semibold">
                                        {book.availableCopies}
                                    </span>
                                </p>

                                <button onClick={() => router.push(`/books/${book.id}`)} className="btn-secondary mt-2">
                                    View Details
                                </button>



                                <div className="mt-5 text-right">
                                    {userBookIds.includes(book.id) ? (
                                        <span className="text-accent-success font-semibold text-lg py-2 px-4 bg-background-primary rounded-md border border-accent-success">
                                            Borrowed
                                        </span>
                                    ) : (
                                        <button
                                            onClick={() => handleBorrow(book.id)}
                                            disabled={book.availableCopies === 0}
                                            className="btn-primary px-5 py-2.5 font-medium text-base disabled:opacity-50 disabled:cursor-not-allowed"
                                        >
                                            {book.availableCopies === 0
                                                ? "Out of Stock"
                                                : "Borrow"}
                                        </button>
                                    )}
                                </div>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
}
