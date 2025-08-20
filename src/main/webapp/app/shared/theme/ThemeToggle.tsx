import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Button } from 'reactstrap';
import { useTheme } from './ThemeProvider';

const ThemeToggle: React.FC = () => {
  const { isDark, toggleTheme } = useTheme();

  return (
    <Button
      color="link"
      onClick={toggleTheme}
      className="theme-toggle-btn p-2 text-decoration-none"
      title={isDark ? 'Switch to light mode' : 'Switch to dark mode'}
    >
      <FontAwesomeIcon icon={isDark ? 'sun' : 'moon'} size="lg" />
    </Button>
  );
};

export default ThemeToggle;
