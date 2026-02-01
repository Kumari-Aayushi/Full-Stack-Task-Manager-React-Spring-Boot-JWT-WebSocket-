import { useEffect } from "react";
import { useTasks } from "../hooks/useTasks";
import { connectSocket } from "../services/websocket";

export default function Dashboard() {
  const { tasks, fetchTasks } = useTasks();

  useEffect(() => {
    fetchTasks();
  }, []);

  useEffect(() => {
    const client = connectSocket(() => {
      fetchTasks(); // refresh tasks on any websocket update
    });

    return () => client.disconnect(() => {});
  }, []);

  return (
    <div style={{ padding: 20 }}>
      <h2>✅ Task Dashboard</h2>

      {tasks.map((t) => (
        <div key={t.id} style={{ border: "1px solid #ddd", margin: 10, padding: 10 }}>
          <h4>{t.title}</h4>
          <p>{t.description}</p>
          <p>Status: {t.status}</p>
          <p>Priority: {t.priority}</p>
        </div>
      ))}
    </div>
  );
}
