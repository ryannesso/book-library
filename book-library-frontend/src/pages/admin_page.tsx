import React, { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/router";

type Book = {
    id: number;
    title: string;
    author: string;
    description: string;
    price: number;
    availableCopies: number;
    status: boolean;
};

type User = {
    id: number;
    name: string;
    email: string;
    role: string;
    borrowBooks: number;
    credits: number;
};

export default function AdminPage() {
    const router = useRouter();
    const [tab, setTab] = useState<"books" | "users" | "stats">("books");

    const [books, setBooks] = useState<Book[]>([]);
    const [users, setUsers] = useState<User[]>([]);
    const [stats, setStats] = useState({ totalBooks: 0, totalUsers: 0, borrowed: 0 });

    const [modalOpen, setModalOpen] = useState(false);
    const [editBookMode, setEditBookMode] = useState(false);
    const [currentBook, setCurrentBook] = useState<Partial<Book>>({});

    const [userModalOpen, setUserModalOpen] = useState(false);
    const [editUserMode, setEditUserMode] = useState(false);
    const [currentUser, setCurrentUser] = useState<Partial<User>>({});
    const [currentPassword, setCurrentPassword] = useState<string>("");

    const [loadingUser, setLoadingUser] = useState(true);
    const [accessDenied, setAccessDenied] = useState(false);

    // Проверка авторизации админа
    useEffect(() => {
        const fetchCurrentUser = async () => {
            try {
                const res = await axios.get<User>("http://localhost:8081/api/users/me", { withCredentials: true });
                if (res.data.role !== "ADMIN") {
                    setAccessDenied(true);
                    router.push("/page");
                }
            } catch (err) {
                console.error("Failed to fetch current user", err);
                router.push("/login_page");
            } finally {
                setLoadingUser(false);
            }
        };
        fetchCurrentUser();
    }, [router]);

    // Загрузка данных только если админ
    useEffect(() => {
        if (loadingUser || accessDenied) return;
        fetchBooks();
        fetchUsers();
        fetchStats();
    }, [loadingUser, accessDenied]);

    const fetchBooks = async () => {
        const res = await axios.get("http://localhost:8081/api/books/all", { withCredentials: true });
        setBooks(res.data);
    };

    const fetchUsers = async () => {
        const res = await axios.get("http://localhost:8081/api/users/all", { withCredentials: true });
        setUsers(res.data);
    };

    const fetchStats = async () => {
        const res = await axios.get("http://localhost:8081/api/admin/stats", { withCredentials: true });
        setStats(res.data);
    };

    // --- Book Modals
    const openAddModal = () => {
        setEditBookMode(false);
        setCurrentBook({});
        setModalOpen(true);
    };

    const openEditModal = (book: Book) => {
        setEditBookMode(true);
        setCurrentBook(book);
        setModalOpen(true);
    };

    const saveBook = async () => {
        if (editBookMode) {
            await axios.put(`http://localhost:8081/api/books/${currentBook.id}`, currentBook, { withCredentials: true });
        } else {
            await axios.post("http://localhost:8081/api/books/create", currentBook, { withCredentials: true });
        }
        setModalOpen(false);
        fetchBooks();
    };

    const deleteBook = async (id: number) => {
        if (confirm("Delete this book?")) {
            await axios.delete(`http://localhost:8081/api/books/${id}`, { withCredentials: true });
            fetchBooks();
        }
    };

    // --- User Modals
    const openAddModalUser = () => {
        setEditUserMode(false);
        setCurrentUser({});
        setCurrentPassword("");
        setUserModalOpen(true);
    };

    const openEditModalUser = (user: User) => {
        setEditUserMode(true);
        setCurrentUser(user);
        setCurrentPassword("");
        setUserModalOpen(true);
    };

    const saveUser = async () => {
        const payload: any = { ...currentUser };
        if (!editUserMode) {
            payload.password = currentPassword; // только при создании
        }

        if (editUserMode) {
            await axios.put(`http://localhost:8081/api/users/${currentUser.id}`, payload, { withCredentials: true });
        } else {
            await axios.post("http://localhost:8081/api/users/create_user", payload, { withCredentials: true });
        }
        setUserModalOpen(false);
        fetchUsers();
    };

    if (loadingUser) {
        return <div className="min-h-screen flex items-center justify-center text-xl text-text-light">Loading...</div>;
    }

    if (accessDenied) {
        return <div className="min-h-screen flex items-center justify-center text-xl text-accent-danger">Access Denied</div>;
    }

    return (
        <div className="bg-background-primary min-h-screen text-text-light">
            {/* Header */}
            <div className="w-full h-[80px] bg-background-secondary flex items-center justify-between px-8 shadow-2xl">
                <h1 className="text-3xl font-extrabold">Admin Panel</h1>
                <button onClick={() => router.push("/page")} className="btn-primary py-2.5 px-6">
                    Back to Library
                </button>
            </div>

            {/* Tabs */}
            <div className="flex gap-6 p-6">
                <button onClick={() => setTab("books")} className={tab === "books" ? "btn-primary" : "btn-secondary"}>Books</button>
                <button onClick={() => setTab("users")} className={tab === "users" ? "btn-primary" : "btn-secondary"}>Users</button>
                <button onClick={() => setTab("stats")} className={tab === "stats" ? "btn-primary" : "btn-secondary"}>Stats</button>
            </div>

            {/* Books Tab */}
            {tab === "books" && (
                <div className="p-6">
                    <div className="flex justify-between mb-4">
                        <h2 className="text-2xl font-bold">Manage Books</h2>
                        <button onClick={openAddModal} className="btn-success px-4 py-2">+ Add Book</button>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {books.map(book => (
                            <div key={book.id} className="card-base p-6">
                                <h3 className="text-xl font-bold">{book.title}</h3>
                                <p className="text-text-muted">Author: {book.author}</p>
                                <p className="text-text-muted">Price: ${book.price}</p>
                                <p className="text-text-muted">Copies: {book.availableCopies}</p>
                                <div className="flex gap-2 mt-4">
                                    <button onClick={() => openEditModal(book)} className="btn-primary px-4 py-2">Edit</button>
                                    <button onClick={() => deleteBook(book.id)} className="btn-danger px-4 py-2">Delete</button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Users Tab */}
            {tab === "users" && (
                <div className="p-6">
                    <div className="flex justify-between mb-4">
                        <h2 className="text-2xl font-bold">Manage Users</h2>
                        <button onClick={openAddModalUser} className="btn-success px-4 py-2">+ Add User</button>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {users.map(user => (
                            <div key={user.id} className="card-base p-6">
                                <h3 className="text-xl">{user.name}</h3>
                                <p className="text-text-muted">{user.email}</p>
                                <p className="text-text-muted">Books borrowed: {user.borrowBooks}</p>
                                <p className="text-text-muted">Credits: {user.credits}</p>
                                <p className="text-text-muted">Role: {user.role}</p>
                                <div className="flex gap-2 mt-4">
                                    <button onClick={() => openEditModalUser(user)} className="btn-primary px-4 py-2">Edit</button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* User Modal */}
            {userModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
                    <div className="card-base p-6 w-[500px]">
                        <h2 className="text-2xl mb-4">{editUserMode ? "Edit User" : "Add User"}</h2>
                        <input
                            type="text"
                            placeholder="Name"
                            value={currentUser.name || ""}
                            onChange={e => setCurrentUser({ ...currentUser, name: e.target.value })}
                            className="w-full mb-2 px-3 py-2 bg-background-primary border border-border-color text-text-light"
                        />
                        <input
                            type="email"
                            placeholder="Email"
                            value={currentUser.email || ""}
                            onChange={e => setCurrentUser({ ...currentUser, email: e.target.value })}
                            className="w-full mb-2 px-3 py-2 bg-background-primary border border-border-color text-text-light"
                        />
                        {!editUserMode && (
                            <input
                                type="password"
                                placeholder="Password"
                                value={currentPassword}
                                onChange={e => setCurrentPassword(e.target.value)}
                                className="w-full mb-2 px-3 py-2 bg-background-primary border border-border-color text-text-light"
                            />
                        )}
                        <input
                            type="number"
                            placeholder="Credits"
                            value={currentUser.credits || 0}
                            onChange={e => setCurrentUser({ ...currentUser, credits: Number(e.target.value) })}
                            className="w-full mb-2 px-3 py-2 bg-background-primary border border-border-color text-text-light"
                        />
                        <select
                            value={currentUser.role || "USER"}
                            onChange={e => setCurrentUser({ ...currentUser, role: e.target.value })}
                            className="w-full mb-4 px-3 py-2 bg-background-primary border border-border-color text-text-light"
                        >
                            <option value="USER">USER</option>
                            <option value="ADMIN">ADMIN</option>
                        </select>
                        <div className="flex justify-end gap-2">
                            <button onClick={() => setUserModalOpen(false)} className="btn-secondary px-4 py-2">Cancel</button>
                            <button onClick={saveUser} className="btn-success px-4 py-2">Save</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Book Modal */}
            {modalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
                    <div className="card-base p-6 w-[500px]">
                        <h2 className="text-2xl mb-4">{editBookMode ? "Edit Book" : "Add Book"}</h2>
                        <input
                            type="text"
                            placeholder="Title"
                            value={currentBook.title || ""}
                            onChange={e => setCurrentBook({ ...currentBook, title: e.target.value })}
                            className="w-full mb-2 px-3 py-2 bg-background-primary border border-border-color text-text-light"
                        />
                        <input
                            type="text"
                            placeholder="Author"
                            value={currentBook.author || ""}
                            onChange={e => setCurrentBook({ ...currentBook, author: e.target.value })}
                            className="w-full mb-2 px-3 py-2 bg-background-primary border border-border-color text-text-light"
                        />
                        <textarea
                            placeholder="Description"
                            value={currentBook.description || ""}
                            onChange={e => setCurrentBook({ ...currentBook, description: e.target.value })}
                            className="w-full mb-2 px-3 py-2 bg-background-primary border border-border-color text-text-light"
                        />
                        <input
                            type="number"
                            placeholder="Price"
                            value={currentBook.price || ""}
                            onChange={e => setCurrentBook({ ...currentBook, price: Number(e.target.value) })}
                            className="w-full mb-2 px-3 py-2 bg-background-primary border border-border-color text-text-light"
                        />
                        <input
                            type="number"
                            placeholder="Copies"
                            value={currentBook.availableCopies || ""}
                            onChange={e => setCurrentBook({ ...currentBook, availableCopies: Number(e.target.value) })}
                            className="w-full mb-4 px-3 py-2 bg-background-primary border border-border-color text-text-light"
                        />
                        <div className="flex justify-end gap-2">
                            <button onClick={() => setModalOpen(false)} className="btn-secondary px-4 py-2">Cancel</button>
                            <button onClick={saveBook} className="btn-success px-4 py-2">Save</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
