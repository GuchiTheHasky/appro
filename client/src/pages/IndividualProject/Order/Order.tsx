import { memo } from 'react'
import { useTranslation } from 'react-i18next';

import classes from './Order.module.scss'
import { ContactForm } from './ContactForm/ContactForm'

export const Order = memo(() => {
  const {t} = useTranslation();
  return (
		<div className={classes['order']}>
			<h3 className={classes['order__title']}>{t('individual.order.title')}</h3>
			<div className={classes['order__description']}>
				<p>{t('individual.order.description1')}</p>

				<p className={classes['order__phone']}>
					<a href='tel:+38 (050) 268 49 26'>+38 (050) 268 49 26</a>
				</p>
				<p className={classes['order__phone']}>
					<a href='tel:+38 (095) 268 49 26'>+38 (095) 268 49 26</a>
				</p>

				<p>{t('individual.order.description2')}</p>
			</div>

			<ContactForm />
		</div>
	)
});
