import "./styles.css";

type PageBannerProps = {
  title: string;
};

export default function PageBanner({ title }: PageBannerProps) {
  return (
    <div className="page-banner">
      <div>
        <p className="page-banner-kicker">Panel operativo</p>
        <h1>{title}</h1>
      </div>
      <span className="page-banner-date">
        {new Date().toLocaleDateString("es-ES", {
          weekday: "long",
          day: "numeric",
          month: "long",
        })}
      </span>
    </div>
  );
}