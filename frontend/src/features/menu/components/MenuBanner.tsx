import "../styles.css";

export default function Banner() {
  return (
    <header className="banner">
      <div className="banner-brand">
        <span className="banner-mark">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
            <path d="M7 3v7a2 2 0 0 0 2 2h0a2 2 0 0 0 2-2V3" />
            <path d="M9 12v9" />
            <path d="M16 3c-1.2 1.4-1.8 3-1.8 5.5 0 2 .8 3 1.8 3.5v8" />
          </svg>
        </span>
        <h1 className="title">PideTú</h1>
      </div>
    </header>
  );
}